package test;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import lombok.Data;
import lombok.NonNull;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.Collectors;

public class OverridesUpdater {
    public static void main(String[] args) throws IOException {
        Map<CompoundTag, CompoundTag> originalTags = new LinkedHashMap<>();
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            //noinspection unchecked
            ListTag<CompoundTag> tags = (ListTag<CompoundTag>) NBTIO.readTag(stream, ByteOrder.LITTLE_ENDIAN, false);
            for (CompoundTag tag : tags.getAll()) {
                originalTags.put(tag.getCompound("block").remove("version"), tag);
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        Map<CompoundTag, BlockInfo> infoList = new LinkedHashMap<>();
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states_overrides.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            ListTag<CompoundTag> states;
            try (BufferedInputStream buffered = new BufferedInputStream(stream)) {
                states = NBTIO.read(buffered).getList("Overrides", CompoundTag.class);
            }

            for (CompoundTag override : states.getAll()) {
                if (override.contains("block") && override.contains("LegacyStates")) {
                    CompoundTag key = override.getCompound("block").remove("version");
                    CompoundTag original = originalTags.get(key);
                    BlockInfo data = new BlockInfo(key, original,
                            original.getList("LegacyStates", CompoundTag.class),
                            override.getList("LegacyStates", CompoundTag.class));
                    BlockInfo removed = infoList.put(key, data);
                    if (removed != null) {
                        throw new IllegalStateException(removed.toString()+"\n"+data.toString());
                    }
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        ListTag<CompoundTag> newOverrides = new ListTag<>("Overrides");

        Map<String, Integer> newBlocks = Arrays.stream(BlockID.class.getDeclaredFields())
                .map(field -> {
                    try {
                        return new AbstractMap.SimpleEntry<>("minecraft:"+field.getName().toLowerCase(), field.getInt(null));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(e-> e.getValue() >= 477)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

      
      
        for (BlockInfo info : infoList.values()) {
            String stateName = info.getStateName();

            CompoundTag override = new CompoundTag();
            override.putCompound("block", info.getKey().copy());
            override.putList((ListTag<? extends Tag>) info.getOverride().copy());

            /*switch (stateName) {
                case "minecraft:light_block;block_light_level=14":
                    break;
                case "minecraft:wood;wood_type=acacia;stripped_bit=0;pillar_axis=y":
                case "minecraft:wood;wood_type=birch;stripped_bit=0;pillar_axis=y":
                case "minecraft:wood;wood_type=dark_oak;stripped_bit=0;pillar_axis=y":
                case "minecraft:wood;wood_type=jungle;stripped_bit=0;pillar_axis=y":
                case "minecraft:wood;wood_type=oak;stripped_bit=0;pillar_axis=y":
                case "minecraft:wood;wood_type=spruce;stripped_bit=0;pillar_axis=y":
                    continue;
            }*/

            newOverrides.add(override);
        }
      
        SortedMap<String, CompoundTag> sorted = new TreeMap<>(new HumanStringComparator());
        for (CompoundTag tag : originalTags.values()) {
            sorted.put(new BlockInfo(tag.getCompound("block"), tag, new ListTag<>(), new ListTag<>()).getStateName(), tag);
        }

        for (CompoundTag tag : sorted.values()) {
            String name = tag.getCompound("block").getString("name");

            Integer blockId = newBlocks.remove(name);
            if (blockId == null) {
                continue;
            }
            
            CompoundTag override = new CompoundTag();
            override.putCompound("block", tag.getCompound("block").remove("version"));
            override.putList(new ListTag<>("LegacyStates").add(new CompoundTag().putInt("id", blockId).putInt("val", 0)));
            newOverrides.add(override);
        }

        byte[] bytes = NBTIO.write(new CompoundTag().putList(newOverrides));
        try(FileOutputStream fos = new FileOutputStream("runtime_block_states_overrides.dat")) {
            fos.write(bytes);
        }
    }

    @Data
    static class BlockInfo {
        @NonNull
        private CompoundTag key;
        @NonNull
        private CompoundTag fullData;
        @NonNull
        private ListTag<CompoundTag> original;
        @NonNull
        private ListTag<CompoundTag> override;

        public String getStateName() {
            StringBuilder stateName = new StringBuilder(key.getString("name"));
            for (Tag tag : key.getCompound("states").getAllTags()) {
                stateName.append(';').append(tag.getName()).append('=').append(tag.parseValue());
            }
            return stateName.toString();
        }
    }
}