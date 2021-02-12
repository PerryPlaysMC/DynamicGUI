package perryplaysmc.dynamicgui.utils.nbt.types;


import perryplaysmc.dynamicgui.utils.nbt.NBTBase;

/**
 * Creator: PerryPlaysMC
 * Created: 10/2020
 **/
@SuppressWarnings("unused")
public class NBTString implements NBTBase {

    private final String value;

    public NBTString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public String toString() {
        return convert(getValue());
    }

    @Override
    public byte getTypeId() {
        return 7;
    }

    public static String convert(String var0) {
        StringBuilder var1 = new StringBuilder(" ");
        char var2 = 0;

        for(int var3 = 0; var3 < var0.length(); ++var3) {
            char var4 = var0.charAt(var3);
            if (var4 == '\\') {
                var1.append('\\');
            } else if (var4 == '"' || var4 == '\'') {
                if (var2 == 0) var2 = (char) (var4 == '"' ? 39 : 34);
                if (var2 == var4) var1.append('\\');
            }
            var1.append(var4);
        }

        if (var2 == 0) {
            var2 = 34;
        }

        var1.setCharAt(0, (char)var2);
        var1.append((char)var2);
        return var1.toString();
    }

}
