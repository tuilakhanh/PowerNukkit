package cn.nukkit.entity.passive;

import cn.nukkit.api.Since;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author Erik Miller | EinBexiii
 */
@Since("1.3.1.0-PN")
public class EntityStrider extends EntityAnimal {

    public final static int NETWORK_ID = 125;

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    public EntityStrider(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(15);
    }

    @Override
    public float getWidth() {
        return 0.9f;
    }

    @Override
    public float getHeight() {
        return 1.7f;
    }

    @Override
    public String getName() {
        return "Strider";
    }
}
