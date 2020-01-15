package cn.nukkit.network.protocol;

import lombok.ToString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@ToString
public class MoveEntityDeltaPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.MOVE_ENTITY_DELTA_PACKET;

    public static final int FLAG_HAS_X = 0b1;
    public static final int FLAG_HAS_Y = 0b10;
    public static final int FLAG_HAS_Z = 0b100;
    public static final int FLAG_HAS_YAW = 0b1000;
    public static final int FLAG_HAS_HEAD_YAW = 0b10000;
    public static final int FLAG_HAS_PITCH = 0b100000;

    public long eid = 0;
    public int flags = 0;
    public int xDelta = 0;
    public int yDelta = 0;
    public int zDelta = 0;
    public double yawDelta = 0;
    public double headYawDelta = 0;
    public double pitchDelta = 0;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.eid = getUnsignedVarLong();
        this.flags = getLShort();
        this.xDelta = getCoordinate(FLAG_HAS_X);
        this.yDelta = getCoordinate(FLAG_HAS_Y);
        this.zDelta = getCoordinate(FLAG_HAS_Z);
        this.yawDelta = getRotation(FLAG_HAS_YAW);
        this.headYawDelta = getRotation(FLAG_HAS_HEAD_YAW);
        this.pitchDelta = getRotation(FLAG_HAS_PITCH);
    }

    @Override
    public void encode() {
        putUnsignedVarLong(eid);
        putLShort(flags);
        putCoordinate(FLAG_HAS_X, this.xDelta);
        putCoordinate(FLAG_HAS_Y, this.yDelta);
        putCoordinate(FLAG_HAS_Z, this.zDelta);
        putRotation(FLAG_HAS_YAW, this.yawDelta);
        putRotation(FLAG_HAS_HEAD_YAW, this.headYawDelta);
        putRotation(FLAG_HAS_PITCH, this.pitchDelta);
    }

    public void calculateFlags() {
        int flags = 0;
        flags |= xDelta != 0 ? FLAG_HAS_X : 0;
        flags |= yDelta != 0 ? FLAG_HAS_Y : 0;
        flags |= zDelta != 0 ? FLAG_HAS_Z : 0;
        flags |= yawDelta != 0 ? FLAG_HAS_YAW : 0;
        flags |= headYawDelta != 0 ? FLAG_HAS_HEAD_YAW : 0;
        flags |= pitchDelta != 0 ? FLAG_HAS_PITCH : 0;
        this.flags = flags;
    }

    private int getCoordinate(int flag) {
        if ((flags & flag) != 0) {
            return this.getVarInt();
        }
        return 0;
    }

    private double getRotation(int flag) {
        if ((flags & flag) != 0) {
            return this.getByte() * (360d / 256d);
        }
        return 0d;
    }

    private void putCoordinate(int flag, int value) {
        if ((flags & flag) != 0) {
            this.putVarInt(value);
        }
    }

    private void putRotation(int flag, double value) {
        if ((flags & flag) != 0) {
            this.putByte((byte) (value / (360d / 256d)));
        }
    }

    public static double decodeCoordinateDelta(int delta) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        if ((delta & 1) == 0) {
            buffer.putInt(delta >> 1);
        } else {
            buffer.putInt(~(delta >> 1));
        }
        buffer.putInt(0);
        buffer.flip();
        double result = buffer.getDouble();
        return result;
    }

    public static int encodeCoordinateDelta(double from, double to) {
        return 0;
    }

    public static int encodeRotationDelta(double from, double to) {
        return 0;
    }
}
