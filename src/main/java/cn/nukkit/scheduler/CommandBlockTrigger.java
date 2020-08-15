package cn.nukkit.scheduler;

import cn.nukkit.ICommandBlock;

public class CommandBlockTrigger extends Task {

    private final ICommandBlock commandBlock;
    private final int chain;

    public CommandBlockTrigger(ICommandBlock commandBlock, int chain) {
        this.commandBlock = commandBlock;
        this.chain = chain;
    }

    @Override
    public void onRun(int currentTick) {
        this.commandBlock.execute(this.chain);
    }
}
