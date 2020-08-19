package cn.nukkit.level.format.updater;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.ChunkSection;
import lombok.RequiredArgsConstructor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
@RequiredArgsConstructor
public class MesaBiomeUpdater implements Updater {
    private final ChunkSection section;
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean update(int offsetX, int offsetY, int offsetZ, int x, int y, int z, BlockState state) {
        if (state.getBlockId() == 48 && state.getLegacyDamage() == 48) {
            section.setBlockState(x, y, z, BlockState.of(BlockID.RED_SANDSTONE));
            return true;
        }
        return false;
    }
}
