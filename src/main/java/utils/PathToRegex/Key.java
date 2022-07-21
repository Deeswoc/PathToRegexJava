package utils.PathToRegex;

public class Key {
    private String name;
    private boolean optional;
    private int offset;

    public Key(String name, boolean optional, int offset) {
        this.name = name;
        this.optional = optional;
        this.offset = offset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
