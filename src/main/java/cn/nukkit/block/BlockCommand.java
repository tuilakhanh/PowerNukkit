package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created by good777LUCKY
 */
public class BlockCommand extends BlockCommandBase {

    @Override
    public String getName() {
        return "Command Block";
    }
    
    @Override
    public int getId() {
        return COMMAND_BLOCK;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
    
    // TODO: Add Functionality
}
