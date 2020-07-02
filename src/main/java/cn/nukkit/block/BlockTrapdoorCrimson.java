package cn.nukkit.block;

public class BlockTrapdoorCrimson extends BlockTrapdoor {
    public BlockTrapdoorCrimson() {
        this(0);
    }
    
    public BlockTrapdoorCrimson(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return CRIMSON_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Crimson Trapdoor";
    }
}
