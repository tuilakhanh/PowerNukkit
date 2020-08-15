package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

/**
 * Created by good777LUCKY
 */
public class BlockCommandRepeating extends BlockCommandBase {

    @Override
    public String getName() {
        return "Repeating Command Block";
    }
    
    @Override
    public int getId() {
        return REPEATING_COMMAND_BLOCK;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }
    
    // TODO: Add Functionality
}
