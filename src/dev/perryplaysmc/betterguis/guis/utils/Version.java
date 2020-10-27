package dev.perryplaysmc.betterguis.guis.utils;

import org.bukkit.Bukkit;

public enum Version {
    v1_7(170), v1_7_R2(172), v1_7_R4(174), v1_7_R10(1710),
    v1_8(180), v1_8_R1(181), v1_8_R2(182), v1_8_R3(183),
    v1_9(190), v1_9_R1(191), v1_9_R2(192),
    v1_10(1100), v1_10_R1(1101),
    v1_11(1110), v1_11_R1(1111),
    v1_12(1120), v1_12_R1(1121),
    v1_13(1130), v1_13_R1(1131), v1_13_R2(1132),
    v1_14(1140), v1_14_R1(1141),
    v1_15(1150), v1_15_R1(1151),
    v1_16(1160), v1_16_R1(1161), v1_16_R2(1162),
    v1_17(1170), v1_17_R1(1171), UNKNOWN(Integer.MAX_VALUE, "Unknown");

    private int ver;
    private String version;

    Version(int ver) {
        this.ver = ver;
        this.version = "v"+name().toUpperCase().substring(1);
    }
    Version(int ver, String version) {
        this.version = version;
        this.ver = ver;
    }

    public String getVersion() {
        return version;
    }

    public int getVersionInt() {
        return ver;
    }
    public boolean isVersion(Version v) {
        return getVersionInt() == v.getVersionInt();
    }

    public boolean isHigher(Version v) {
        return getVersionInt()>v.getVersionInt();
    }

    public boolean isLower(Version v) {
        return getVersionInt()<v.getVersionInt();
    }


    public static boolean isCurrent(Version v) {
        if(v.name().contains("R"))
            return getCurrentVersionExact().isVersion(v);
        return getCurrentVersion().isVersion(v);
    }

    public static boolean isCurrentLower(Version v) {
        return getCurrentVersion().isLower(v);
    }

    public static boolean isCurrentHigher(Version v) {
        if(v.name().contains("R"))
            return getCurrentVersionExact().isHigher(v);
        return getCurrentVersion().isHigher(v);
    }

    public static String getNMSPackage() {
        return "net.minecraft.server." + getCurrentVersionExact().getVersion();
    }
    public static String getCBPackage() {
        return "org.bukkit.craftbukkit." + getCurrentVersionExact().getVersion();
    }


    public static Version getCurrentVersionExact() {
        String pack =  Bukkit.getServer().getClass().getPackage().getName();
        String version = pack.substring(pack.lastIndexOf('.')+1).replaceFirst("v", "");
        Version ret = Version.UNKNOWN;
        switch(version) {
            case "1_7_2": {
                ret = Version.v1_7_R2;
                break;
            }
            case "1_7_R4": {
                ret = Version.v1_7_R4;
                break;
            }
            case "1_7_R10": {
                ret = Version.v1_7_R10;
                break;
            }
            case "1_8_R1": {
                ret = Version.v1_8_R1;
                break;
            } case "1_8_R2": {
                ret = Version.v1_8_R2;
                break;
            } case "1_8_R3": {
                ret = Version.v1_8_R3;
                break;
            } case "1_9_R1": {
                ret = Version.v1_9_R1;
                break;
            } case "1_9_R2": {
                ret = Version.v1_9_R2;
                break;
            } case "1_10_R1": {
                ret = Version.v1_10_R1;
                break;
            } case "1_11_R1": {
                ret = Version.v1_11_R1;
                break;
            } case "1_12_R1": {
                ret = Version.v1_12_R1;
                break;
            } case "1_13_R1": {
                ret = Version.v1_13_R1;
                break;
            } case "1_13_R2": {
                ret = Version.v1_13_R2;
                break;
            } case "1_14_R1": {
                ret = Version.v1_14_R1;
                break;
            } case "1_15_R1": {
                ret = Version.v1_15_R1;
                break;
            } case "1_16_R1": {
                ret = Version.v1_16_R1;
                break;
            } case "1_16_R2": {
                ret = Version.v1_16_R2;
                break;
            } case "1_17_R1": {
                ret = Version.v1_17_R1;
                break;
            }
            default: {
                ret.version = version;
                ret.ver = Integer.parseInt(version.toLowerCase().replace(("_"),("")).replace(("r"),("")));
                break;
            }
        }
        return ret;
    }

    public static Version getCurrentVersion() {
        String pack =  Bukkit.getServer().getClass().getPackage().getName();
        String version = pack.substring(pack.lastIndexOf('.')+1).replaceFirst("v", "");
        Version ret = Version.UNKNOWN;
        switch(version) {
            case "1_8_R1":
            case "1_8_R3":
            case "1_8_R2": {
                ret = Version.v1_8;
                break;
            }
            case "1_9_R1":
            case "1_9_R2": {
                ret = Version.v1_9;
                break;
            }
            case "1_10_R1": {
                ret = Version.v1_10;
                break;
            } case "1_11_R1": {
                ret = Version.v1_11;
                break;
            } case "1_12_R1": {
                ret = Version.v1_12;
                break;
            } case "1_13_R1":
            case "1_13_R2": {
                ret = Version.v1_13;
                break;
            }
            case "1_14_R1": {
                ret = Version.v1_14;
                break;
            } case "1_15_R1": {
                ret = Version.v1_15;
                break;
            } case "1_16_R1":
            case "1_16_R2": {
                ret = Version.v1_16;
                break;
            } case "1_17_R1": {
                ret = Version.v1_17;
                break;
            } default: {
                ret.version = version;
                ret.ver = Integer.parseInt(version.toLowerCase().split("R")[0].replace(("_"),("")));
            }
        }
        return ret;
    }


    public static Version value(String versionId) {
        for(Version version : values()) {
            if(versionId.equalsIgnoreCase(version.name()) || versionId.equalsIgnoreCase(version.getVersion())) return version;
        }
        return null;
    }

}