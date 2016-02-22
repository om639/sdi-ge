package com.sdi.ge.net.packets;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Oliver on 19/02/2016.
 */
public class InboundPacket {

    private Type type;

    private ByteBuf payload;

    public InboundPacket(Type type, ByteBuf payload) {
        if (payload.readableBytes() < type.getSize()) {
            throw new IllegalArgumentException("invalid payload size");
        }

        this.type = type;
        this.payload = payload;
    }

    public Type getType() {
        return type;
    }

    public ByteBuf getPayload() {
        return payload;
    }

    public void release() {
        payload.release();
    }

    public enum Type {
        MOVEMENT(1, 1),
        ATTACK(2, 16),
        FLIGHT(3, 0);

        /**
         * Maps opcodes to packet types.
         */
        private static final Map<Integer, Type> opcodes = new HashMap<>();

        /**
         * Populates the opcode map.
         */
        static {
            for (Type type : Type.values()) {
                opcodes.put(type.getOpcode(), type);
            }
        }

        public static Type forOpcode(int opcode) {
            return opcodes.get(opcode);
        }

        private int opcode, size;

        Type(int opcode, int size) {
            this.opcode = opcode;
            this.size = size;
        }

        public int getOpcode() {
            return opcode;
        }

        public int getSize() {
            return size;
        }
    }
}
