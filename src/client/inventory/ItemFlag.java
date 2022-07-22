package client.inventory;

public enum ItemFlag{
    LOCK(0x01),//锁
    SPIKES(0x02),//防滑
    COLD(0x04),
    UNTRADEABLE(0x08),//不可交易
    KARMA_EQ(0x10),
    KARMA_USE(0x02);
    
    private final int i;
    
    private ItemFlag(final int i) {
        this.i = i;
    }
    
    public final int getValue() {
        return this.i;
    }
    
    public final boolean check(final int flag) {
        return (flag & this.i) == this.i;
    }
}
