package test;

import cn.nukkit.network.protocol.MoveEntityDeltaPacket;

public class DeltaTest {
    public static void main(String[] args) {
        System.out.println(MoveEntityDeltaPacket.decodeCoordinateDelta(55553));
        System.out.println(MoveEntityDeltaPacket.decodeCoordinateDelta(49211));
        System.out.println(MoveEntityDeltaPacket.decodeCoordinateDelta(-207907));
    }
}
