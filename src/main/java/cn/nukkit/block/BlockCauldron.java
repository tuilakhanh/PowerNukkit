package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCauldron;
import cn.nukkit.event.player.PlayerBucketEmptyEvent;
import cn.nukkit.event.player.PlayerBucketFillEvent;
import cn.nukkit.item.*;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author CreeperFace (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockCauldron extends BlockSolidMeta implements BlockEntityHolder<BlockEntityCauldron> {

    public BlockCauldron() {
        super(0);
    }

    public BlockCauldron(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CAULDRON_BLOCK;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.CAULDRON;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntityCauldron> getBlockEntityClass() {
        return BlockEntityCauldron.class;
    }

    public String getName() {
        return "Cauldron Block";
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    public boolean isFull() {
        return (this.getDamage() & 0x06) == 0x06;
    }

    public boolean isEmpty() {
        return this.getDamage() == 0x00;
    }
    
    public int getFillLevel() {
        return (getDamage() & 0x6) >> 1;
    }
    
    public void setFillLevel(int fillLevel) {
        fillLevel = MathHelper.clamp(fillLevel, 0, 3);
        setDamage(fillLevel << 1);
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        BlockEntityCauldron cauldron = getBlockEntity();

        if (cauldron == null) {
            return false;
        }

        switch (item.getId()) {
            case ItemID.BUCKET:
                if (item.getDamage() == 0) {//empty bucket
                    if (!isFull() || cauldron.isCustomColor() || cauldron.hasPotion()) {
                        break;
                    }

                    ItemBucket bucket = (ItemBucket) item.clone();
                    bucket.setCount(1);
                    bucket.setDamage(8);//water bucket

                    PlayerBucketFillEvent ev = new PlayerBucketFillEvent(player, this, null, this, item, bucket);
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        replaceBucket(item, player, ev.getItem());
                        this.setFillLevel(0);//empty
                        this.level.setBlock(this, this, true);
                        cauldron.clearCustomColor();
                        this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_TAKEWATER);
                    }
                } else if (item.getDamage() == 8 || item.getDamage() == 10) {//water and lava buckets
                    if (isFull() && !cauldron.isCustomColor() && !cauldron.hasPotion() && item.getDamage() == 8) {
                        break;
                    }

                    ItemBucket bucket = (ItemBucket) item.clone();
                    bucket.setCount(1);
                    bucket.setDamage(0);//empty bucket

                    PlayerBucketEmptyEvent ev = new PlayerBucketEmptyEvent(player, this, null, this, item, bucket);
                    this.level.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        if (player.isSurvival() || player.isAdventure()) {
                            replaceBucket(item, player, ev.getItem());
                        }
                        if (cauldron.hasPotion()) {//if has potion
                            clearWithFizz(cauldron);
                        } else if (item.getDamage() == 8) { //water bucket
                            this.setFillLevel(3);//fill
                            cauldron.clearCustomColor();
                            this.level.setBlock(this, this, true);
                            this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.CAULDRON_FILLWATER);
                        } else { // lava bucket
                            if (isEmpty()) {
                                BlockCauldronLava cauldronLava = new BlockCauldronLava(0xE);
                                cauldronLava.setFillLevel(3);
                                this.level.setBlock(this, cauldronLava, true, true);
                                cauldron.clearCustomColor();
                                this.getLevel().addSound(this.add(0.5, 1, 0.5), Sound.BUCKET_EMPTY_LAVA);
                            } else {
                                clearWithFizz(cauldron);
                            }
                        }
                        //this.update();
                    }
                }
                break;
            case ItemID.DYE:
                if (isEmpty() || cauldron.hasPotion()) {
                    break;
                }
    
                if (player.isSurvival() || player.isAdventure()) {
                    item.setCount(item.getCount()-1);
                    player.getInventory().setItemInHand(item);
                }
    
                BlockColor color = new ItemDye(item.getDamage()).getDyeColor().getColor();
                if (!cauldron.isCustomColor()) {
                    cauldron.setCustomColor(color);
                } else {
                    BlockColor current = cauldron.getCustomColor();
                    BlockColor mixed = new BlockColor(
                            current.getRed() + (color.getRed() - current.getRed()) / 2,
                            current.getGreen() + (color.getGreen() - current.getGreen()) / 2,
                            current.getBlue() + (color.getBlue() - current.getBlue()) / 2
                    );
                    cauldron.setCustomColor(mixed);
                }
                this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.CAULDRON_ADDDYE);
                
                break;
            case ItemID.LEATHER_CAP:
            case ItemID.LEATHER_TUNIC:
            case ItemID.LEATHER_PANTS:
            case ItemID.LEATHER_BOOTS:
            case ItemID.LEATHER_HORSE_ARMOR:
                if (isEmpty() || cauldron.hasPotion()) {
                    break;
                }
                
                CompoundTag compoundTag = item.hasCompoundTag()? item.getNamedTag() : new CompoundTag();
                compoundTag.putInt("customColor", cauldron.getCustomColor().getRGB());
                item.setCompoundTag(compoundTag);
                player.getInventory().setItemInHand(item);
                
                setFillLevel(getFillLevel() - 1);
                this.level.setBlock(this, this, true, true);
                this.level.addSound(add(0.5, 0.5, 0.5), Sound.CAULDRON_DYEARMOR);
                
                break;
            case ItemID.POTION:
            case ItemID.SPLASH_POTION:
            case ItemID.LINGERING_POTION:
                if (!isEmpty() && (cauldron.hasPotion()? cauldron.getPotionId() != item.getDamage() : item.getDamage() != 0)) {
                    clearWithFizz(cauldron);
                    consumePotion(item, player);
                    break;
                }
                if (isFull()) {
                    break;
                }
                
                if (item.getDamage() != 0 && isEmpty()) {
                    cauldron.setPotionId(item.getDamage());
                }
                
                cauldron.setPotionType(
                        item.getId() == ItemID.POTION? BlockEntityCauldron.POTION_TYPE_NORMAL :
                                item.getId() == ItemID.SPLASH_POTION? BlockEntityCauldron.POTION_TYPE_SPLASH :
                                        BlockEntityCauldron.POTION_TYPE_LINGERING
                );
                cauldron.spawnToAll();
                
                setFillLevel(getFillLevel() + 1);
                this.level.setBlock(this, this, true);
    
                consumePotion(item, player);
    
                this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.CAULDRON_FILLPOTION);
                break;
            case ItemID.GLASS_BOTTLE:
                if (isEmpty()) {
                    break;
                }

                int meta = cauldron.hasPotion() ? cauldron.getPotionId() : 0;
                
                Item potion;
                if (meta == 0) {
                    potion = new ItemPotion();
                } else {
                    switch (cauldron.getPotionType()) {
                        case BlockEntityCauldron.POTION_TYPE_SPLASH:
                            potion = new ItemPotionSplash(meta);
                            break;
                        case BlockEntityCauldron.POTION_TYPE_LINGERING:
                            potion = new ItemPotionLingering(meta);
                            break;
                        case BlockEntityCauldron.POTION_TYPE_NORMAL:
                        default:
                            potion = new ItemPotion(meta);
                            break;
                    }
                }

                setFillLevel(getFillLevel() - 1);
                if (isEmpty()) {
                    cauldron.setPotionId(0xffff);//reset potion
                    cauldron.clearCustomColor();
                }
                this.level.setBlock(this, this, true);
                
                boolean consumeBottle = player.isSurvival() || player.isAdventure();
                if (consumeBottle && item.getCount() == 1) {
                    player.getInventory().setItemInHand(potion);
                } else if (item.getCount() > 1) {
                    if (consumeBottle) {
                        item.setCount(item.getCount() - 1);
                        player.getInventory().setItemInHand(item);
                    }
    
                    if (player.getInventory().canAddItem(potion)) {
                        player.getInventory().addItem(potion);
                    } else {
                        player.getLevel().dropItem(player.add(0, 1.3, 0), potion, player.getDirectionVector().multiply(0.4));
                    }
                }

                this.level.addSound(this.add(0.5, 0.5, 0.5), Sound.CAULDRON_TAKEPOTION);
                break;
            default:
                return true;
        }

        this.level.updateComparatorOutputLevel(this);
        return true;
    }
    
    protected void replaceBucket(Item oldBucket, Player player, Item newBucket) {
        if (player.isSurvival() || player.isAdventure()) {
            if (oldBucket.getCount() == 1) {
                player.getInventory().setItemInHand(newBucket);
            } else {
                oldBucket.setCount(oldBucket.getCount() - 1);
                if (player.getInventory().canAddItem(newBucket)) {
                    player.getInventory().addItem(newBucket);
                } else {
                    player.getLevel().dropItem(player.add(0, 1.3, 0), newBucket, player.getDirectionVector().multiply(0.4));
                }
            }
        }
    }
    
    private void consumePotion(Item item, Player player) {
        if (player.isSurvival() || player.isAdventure()) {
            if (item.getCount() == 1) {
                player.getInventory().setItemInHand(new ItemBlock(new BlockAir()));
            } else if (item.getCount() > 1) {
                item.setCount(item.getCount() - 1);
                player.getInventory().setItemInHand(item);

                Item bottle = new ItemGlassBottle();
                if (player.getInventory().canAddItem(bottle)) {
                    player.getInventory().addItem(bottle);
                } else {
                    player.getLevel().dropItem(player.add(0, 1.3, 0), bottle, player.getDirectionVector().multiply(0.4));
                }
            }
        }
    }
    
    public void clearWithFizz(BlockEntityCauldron cauldron) {
        this.setFillLevel(0);//empty
        cauldron.setPotionId(0xffff);//reset potion
        cauldron.setSplashPotion(false);
        cauldron.clearCustomColor();
        this.level.setBlock(this, new BlockCauldron(0), true);
        this.level.addSound(this.add(0.5, 0, 0.5), Sound.RANDOM_FIZZ);
        for (int i = 0; i < 8; ++i) {
            this.getLevel().addParticle(new SmokeParticle(add(Math.random(), 1.2, Math.random())));
        }
    }
    
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        CompoundTag nbt = new CompoundTag()
                .putShort("PotionId", 0xffff)
                .putByte("SplashPotion", 0);

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        return BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt) != null;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{new ItemCauldron()};
        }

        return new Item[0];
    }

    @Override
    public Item toItem() {
        return new ItemCauldron();
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    
    @Override
    public int getComparatorInputOverride() {
        return getFillLevel();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
