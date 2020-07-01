package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.api.Since;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.inventory.*;
import cn.nukkit.inventory.transaction.action.DamageAnvilAction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.inventory.transaction.action.TakeLevelAction;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.scheduler.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author CreeperFace
 */
public class CraftingTransaction extends InventoryTransaction {

    protected int gridSize;

    protected List<Item> inputs;

    protected List<Item> secondaryOutputs;

    protected Item primaryOutput;

    protected Recipe recipe;

    protected int craftingType;

    public CraftingTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions, false);

        this.craftingType = source.craftingType;
        if (source.craftingType == Player.CRAFTING_STONECUTTER) {
            this.gridSize = 1;
            
            this.inputs = new ArrayList<>(1);
            
            this.secondaryOutputs = new ArrayList<>(1);
        } else {
            this.gridSize = (source.getCraftingGrid() instanceof BigCraftingGrid) ? 3 : 2;

            this.inputs = new ArrayList<>();

            this.secondaryOutputs = new ArrayList<>();
        }

        init(source, actions);
    }
    
    public void setInput(Item item) {
        if (inputs.size() < gridSize * gridSize) {
            for (Item existingInput : this.inputs) {
                if (existingInput.equals(item, item.hasMeta(), item.hasCompoundTag())) {
                    existingInput.setCount(existingInput.getCount() + item.getCount());
                    return;
                }
            }
            inputs.add(item.clone());
        } else {
            throw new RuntimeException("Input list is full can't add " + item);
        }
    }

    public List<Item> getInputList() {
        return inputs;
    }

    public void setExtraOutput(Item item) {
        if (secondaryOutputs.size() < gridSize * gridSize) {
            secondaryOutputs.add(item.clone());
        } else {
            throw new RuntimeException("Output list is full can't add " + item);
        }
    }

    public Item getPrimaryOutput() {
        return primaryOutput;
    }

    public void setPrimaryOutput(Item item) {
        if (primaryOutput == null) {
            primaryOutput = item.clone();
        } else if (!primaryOutput.equals(item)) {
            throw new RuntimeException("Primary result item has already been set and does not match the current item (expected " + primaryOutput + ", got " + item + ")");
        }
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public boolean canExecute() {
        CraftingManager craftingManager = source.getServer().getCraftingManager();
        if (craftingType == Player.CRAFTING_STONECUTTER) {
            recipe = craftingManager.matchStonecutterRecipe(this.primaryOutput);
        } else if (craftingType == Player.CRAFTING_CARTOGRAPHY) {
            recipe = craftingManager.matchCartographyRecipe(inputs, this.primaryOutput, this.secondaryOutputs);
        } else if (craftingType == Player.CRAFTING_ANVIL) {
            Inventory inventory = source.getWindowById(Player.ANVIL_WINDOW_ID);
            if (inventory instanceof AnvilInventory) {
                AnvilInventory anvil = (AnvilInventory) inventory;
                addInventory(anvil);
                if (this.primaryOutput.equalsIgnoringEnchantmentOrder(anvil.getResult(), true)) {
                    TakeLevelAction takeLevel = new TakeLevelAction(anvil.getLevelCost());
                    addAction(takeLevel);
                    if (takeLevel.isValid(source)) {
                        recipe = new RepairRecipe(InventoryType.ANVIL, this.primaryOutput, this.inputs);
                        PlayerUIInventory uiInventory = source.getUIInventory();
                        actions.add(new DamageAnvilAction(anvil, !source.isCreative() && ThreadLocalRandom.current().nextFloat() < 0.12F, this));
                        actions.stream()
                                .filter(a -> a instanceof SlotChangeAction)
                                .map(a-> (SlotChangeAction) a)
                                .filter(a -> a.getInventory() == uiInventory)
                                .filter(a -> a.getSlot() == 50)
                                .findFirst()
                                .ifPresent(a -> {
                                    // Move the set result action to the end, otherwise the result would be cleared too early
                                    actions.remove(a);
                                    actions.add(a);
                                });
                    }
                }
            }
            source.getUIInventory().setItem(AnvilInventory.RESULT, Item.get(0), false);
        } else if (craftingType == Player.CRAFTING_GRINDSTONE) {
            Inventory inventory = source.getWindowById(Player.GRINDSTONE_WINDOW_ID);
            if (inventory instanceof GrindstoneInventory) {
                GrindstoneInventory grindstone = (GrindstoneInventory) inventory;
                addInventory(grindstone);
                if (this.primaryOutput.equals(grindstone.getResult(), true, true)) {
                    recipe = new RepairRecipe(InventoryType.GRINDSTONE, this.primaryOutput, this.inputs);
                    grindstone.setResult(Item.get(0), false);
                }
            }
        } else {
            recipe = craftingManager.matchRecipe(inputs, this.primaryOutput, this.secondaryOutputs);
        }

        return this.recipe != null && super.canExecute();
    }

    protected boolean callExecuteEvent() {
        CraftItemEvent ev;

        this.source.getServer().getPluginManager().callEvent(ev = new CraftItemEvent(this));
        return !ev.isCancelled();
    }

    protected void sendInventories() {
        super.sendInventories();
        
        Optional<Inventory> topWindow = source.getTopWindow();
        if (topWindow.isPresent()) {
            //source.removeWindow(topWindow.get());
            return;
        }
        
		/*
         * TODO: HACK!
		 * we can't resend the contents of the crafting window, so we force the client to close it instead.
		 * So people don't whine about messy desync issues when someone cancels CraftItemEvent, or when a crafting
		 * transaction goes wrong.
		 */
        ContainerClosePacket pk = new ContainerClosePacket();
        pk.windowId = ContainerIds.NONE;
        //TODO This hack causes PowerNukkit#223
        source.getServer().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int currentTick) {
                source.dataPacket(pk);
            }
        }, 20);
        
        this.source.resetCraftingGridType();
    }

    public boolean execute() {
        if (super.execute()) {
            switch (this.primaryOutput.getId()) {
                case Item.CRAFTING_TABLE:
                    source.awardAchievement("buildWorkBench");
                    break;
                case Item.WOODEN_PICKAXE:
                    source.awardAchievement("buildPickaxe");
                    break;
                case Item.FURNACE:
                    source.awardAchievement("buildFurnace");
                    break;
                case Item.WOODEN_HOE:
                    source.awardAchievement("buildHoe");
                    break;
                case Item.BREAD:
                    source.awardAchievement("makeBread");
                    break;
                case Item.CAKE:
                    source.awardAchievement("bakeCake");
                    break;
                case Item.STONE_PICKAXE:
                case Item.GOLDEN_PICKAXE:
                case Item.IRON_PICKAXE:
                case Item.DIAMOND_PICKAXE:
                    source.awardAchievement("buildBetterPickaxe");
                    break;
                case Item.WOODEN_SWORD:
                    source.awardAchievement("buildSword");
                    break;
                case Item.DIAMOND:
                    source.awardAchievement("diamond");
                    break;
            }

            return true;
        }

        return false;
    }

    @Since("1.3.0.0-PN")
    public boolean checkForCraftingPart(List<InventoryAction> actions) {
        for (InventoryAction action : actions) {
            if (action instanceof SlotChangeAction) {
                SlotChangeAction slotChangeAction = (SlotChangeAction) action;
                if (slotChangeAction.getInventory().getType() == InventoryType.UI && slotChangeAction.getSlot() == 50 &&
                        !slotChangeAction.getSourceItem().equals(slotChangeAction.getTargetItem())) {
                    return true;
                }
            }
        }
        return false;
    }
}
