import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import com.google.common.primitives.Ints;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BlockStates {
    public static void main(String[] args) {
        before();
        after();
    }
    
    public static void before() {
        Map<CompoundTag, int[]> metaOverrides = new LinkedHashMap<>();
        /*try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states_overrides.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            ListTag<CompoundTag> states;
            try (BufferedInputStream buffered = new BufferedInputStream(stream)) {
                states = NBTIO.read(buffered).getList("Overrides", CompoundTag.class);
            }

            for (CompoundTag override : states.getAll()) {
                if (override.contains("block") && override.contains("meta")) {
                    metaOverrides.put(override.getCompound("block").remove("version"), override.getIntArray("meta"));
                }
            }

        } catch (IOException e) {
            throw new AssertionError(e);
        }*/

        ListTag<CompoundTag> tags;
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            //noinspection unchecked
            tags = (ListTag<CompoundTag>) NBTIO.readTag(stream, ByteOrder.LITTLE_ENDIAN, false);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        TreeMap<String, String> blockStates = new TreeMap<>();
        for (CompoundTag state : tags.getAll()) {
            StringBuilder data = new StringBuilder();
            data.append(state.copy().remove("meta").toString()).append('\n');
            CompoundTag block = state.getCompound("block");
            StringBuilder blockName = new StringBuilder(block.getString("name"));
            for (Tag tag : block.getCompound("states").getAllTags()) {
                blockName.append(';').append(tag.getName()).append('=').append(tag.parseValue());
            }
            data.append(blockName.toString()).append('\n');
            data.append(Ints.asList(state.getIntArray("meta")).stream().map(i -> state.getInt("id") + ":" + i).collect(Collectors.toList()).toString()).append('\n');
            int[] overrides = metaOverrides.get(block.copy().remove("version"));
            if (overrides != null) {
                data.append(Ints.asList(overrides).toString()).append('\n');
            }
            data.append('\n');
            blockStates.put(blockName.toString(), data.toString());
        }
        
        try(FileWriter writer = new FileWriter("before.txt")) {
            for (String value : blockStates.values()) {
                writer.write(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void after() {
        Map<CompoundTag, int[]> metaOverrides = new LinkedHashMap<>();
        /*try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states_overrides.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            ListTag<CompoundTag> states;
            try (BufferedInputStream buffered = new BufferedInputStream(stream)) {
                states = NBTIO.read(buffered).getList("Overrides", CompoundTag.class);
            }

            for (CompoundTag override : states.getAll()) {
                if (override.contains("block") && override.contains("meta")) {
                    metaOverrides.put(override.getCompound("block").remove("version"), override.getIntArray("meta"));
                }
            }

        } catch (IOException e) {
            throw new AssertionError(e);
        }*/

        ListTag<CompoundTag> tags;
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states_after.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }

            //noinspection unchecked
            tags = (ListTag<CompoundTag>) NBTIO.readTag(stream, ByteOrder.LITTLE_ENDIAN, false);
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        TreeMap<String, String> blockStates = new TreeMap<>();
        for (CompoundTag state : tags.getAll()) {
            StringBuilder data = new StringBuilder();
            data.append(state.copy().remove("LegacyStates").toString()).append('\n');
            CompoundTag block = state.getCompound("block");
            StringBuilder stateName = new StringBuilder(block.getString("name"));
            for (Tag tag : block.getCompound("states").getAllTags()) {
                stateName.append(';').append(tag.getName()).append('=').append(tag.parseValue());
            }
            data.append(stateName.toString()).append('\n');
            List<String> metas = state.getList("LegacyStates", CompoundTag.class).getAll().stream()
                    .map(t -> t.getInt("id") + ":" + t.getShort("val"))
                    .collect(Collectors.toList());
            data.append(metas.toString()).append('\n');
            int[] overrides = metaOverrides.get(block.copy().remove("version"));
            if (overrides != null) {
                data.append(Ints.asList(overrides).toString()).append('\n');
            }
            data.append("\n");
            blockStates.put(stateName.toString(), data.toString());
        }
        
        try(FileWriter writer = new FileWriter("after.txt")) {
            for (String value : blockStates.values()) {
                writer.write(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
