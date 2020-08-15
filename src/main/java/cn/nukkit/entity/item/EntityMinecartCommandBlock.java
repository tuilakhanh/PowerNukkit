package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.MinecartType;

/**
 * Created by good777LUCKY
 */
public class EntityMinecartCommandBlock extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 100;
    
    // TODO: Add Functionality
    
    public EntityMinecartCommandBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setDisplayBlock(Block.get(Block.COMMAND_BLOCK), false);
    }
    
    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(6);
    }
    
    @Override
    public boolean isRideable(){
        return false;
    }
    
    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
    
    @Override
    public boolean mountEntity(Entity entity, byte mode) {
        return false;
    }
    
    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        // TODO: Add UI
        return false;
    }
    
    @Override
    public void initEntity() {
        super.initEntity();
        // TODO: Add Functionality
    }
    
    @Override
    public void saveNBT() {
        super.saveNBT();
        // TODO: Add Functionality
    }
}
