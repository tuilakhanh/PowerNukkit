package cn.nukkit.network.protocol;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class SetSpawnPositionPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_SPAWN_POSITION_PACKET;

    public static final int TYPE_PLAYER_SPAWN = 0;
    public static final int TYPE_WORLD_SPAWN = 1;

    public int spawnType;
    public int y;
    public int z;
    public int x;
    
    @Since("1.2.2.0-PN")
    public int dimension = 0;
    
    @PowerNukkitOnly("Backward compatibility")
    @Deprecated @DeprecationDetails(
            since = "1.2.2.0-PN", toBeRemovedAt = "1.4.0.0-PN",
            reason = "Removed from the Bedrock protocol since 1.16.0")
    public boolean spawnForced = false;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.spawnType);
        this.putBlockVector3(this.x, this.y, this.z);
        this.putVarInt(dimension);
        this.putBlockVector3(this.x, this.y, this.z);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
