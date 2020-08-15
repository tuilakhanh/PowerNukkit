package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created by good777LUCKY
 */
public class BlockCommandChain extends BlockCommandBase {

    @Override
    public String getName() {
        return "Chain Command Block";
    }
    
    @Override
    public int getId() {
        return CHAIN_COMMAND_BLOCK;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }
    
    // TODO: Add Functionality
}
