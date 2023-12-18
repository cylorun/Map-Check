package me.cylorun.utili;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;

import java.awt.geom.Point2D;

public class SeedInfo {
    private long seed;
    public int shCount = 3;

    public SeedInfo(long seed) {
        this.seed = seed;

    }

    public SeedInfo() {
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public long getSeed() {
        return this.seed;
    }

    public int getEyeCount() {
        return 0;
    }

    public CPos[] getStrongholdLocs(int count) {
        ChunkRand rand = new ChunkRand(this.seed);
        OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_1, this.seed);
        Stronghold sh = new Stronghold(MCVersion.v1_16_1);
        return sh.getStarts(biomeSource, count, rand);
    }

    public String coordsStringNether() {
        CPos[] shLocs = this.getStrongholdLocs(shCount);
        StringBuilder builder = new StringBuilder("<html>");
        for (CPos pos : shLocs) {
            int shX = (pos.toBlockPos().getX()+5) / 8;
            int shZ = (pos.toBlockPos().getZ()+5) / 8;
            String netherStr = String.format("<font color='green'> X: </font>%s<font color='green'> Z: </font>%s <br>", shX, shZ);
            builder.append(netherStr);
        }
        builder.append("</html>");
        return builder.toString();
    }

    public String coordsStringOverworld() {
        CPos[] shLocs = this.getStrongholdLocs(shCount);
        StringBuilder builder = new StringBuilder("<html>");
        for (CPos pos : shLocs) {
            int shX = pos.toBlockPos().getX()+5;
            int shZ = pos.toBlockPos().getZ()+5;
            String owStr = String.format("<font color='green'> &emsp;X:</font>%s<font color='green'> Z:</font>%s <br>", shX, shZ);
            builder.append(owStr);
        }
        builder.append("</html>");
        return builder.toString();
    }

    public double getStrongholdAngle(int shX, int shZ, int pX, int pZ) { // usually off by 3-10 degrees, especialy when close
        double angleRadians = Math.atan2(shZ - pZ, shX - pX);
        double angleDegrees = Math.toDegrees(angleRadians);
        angleDegrees -= 90;
        if (angleDegrees > 180) {
            angleDegrees -= 360;
        } else if (angleDegrees <= -180) {
            angleDegrees += 360;
        }

        return angleDegrees;
    }

    public int getStronholdDist(int shX, int shZ, int pX, int pZ) {
        return (int) Math.abs(Point2D.distance(pX, pZ, shX, shZ));
    }

    public String strongholdDistString(int[] playerPos, MCDimension dim) {
        StringBuilder builder = new StringBuilder("<html>");
        CPos[] shs = getStrongholdLocs(shCount);
        for (CPos pos : shs) {
            int xPos = pos.toBlockPos().getX()+5; // +5 to adjust to [4,4] instead of [15,15]
            int zPos = pos.toBlockPos().getZ()+5;
            int dist = dim == MCDimension.NETHER ? getStronholdDist(xPos/8,zPos/8,playerPos[0],playerPos[1]) : getStronholdDist(xPos,zPos,playerPos[0],playerPos[1]);

            builder.append(String.format("&emsp;%s <br>", dist));
        }
        builder.append("</html>");
        return builder.toString();
    }

    public String strongholdAngleString(int[] playerPos, MCDimension dim) {
        StringBuilder builder = new StringBuilder("<html>");
        CPos[] shs = getStrongholdLocs(shCount);
        for (CPos pos : shs) {
            int shX = pos.toBlockPos().getX()+5;
            int shZ = pos.toBlockPos().getZ()+5;
            if (dim == MCDimension.NETHER) {
                shX = (int) shX / 8;
                shZ = (int) shZ / 8;

            }
            double angle = getStrongholdAngle(shX, shZ, playerPos[0], playerPos[1]);
            angle = Math.round(angle * Math.pow(10, 1)) / Math.pow(10, 1);
            builder.append(String.format("&emsp;%s <br>", angle));

        }
        builder.append("</html>");

        return builder.toString();
    }
}


