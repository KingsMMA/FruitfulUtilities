package dev.kingrabbit.fruitfulutilities.util;

public record Region(float x1, float y1, float x2, float y2) {

    public boolean contains(double x, double y) {
        return x1 <= x && x <=  x2 &&
                y1 <= y && y <= y2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Region region = (Region) o;

        if (x1 != region.x1) return false;
        if (y1 != region.y1) return false;
        if (x2 != region.x2) return false;
        return y2 == region.y2;
    }

    @Override
    public int hashCode() {
        float result = x1;
        result = 31 * result + y1;
        result = 31 * result + x2;
        result = 31 * result + y2;
        return (int) result;
    }
}
