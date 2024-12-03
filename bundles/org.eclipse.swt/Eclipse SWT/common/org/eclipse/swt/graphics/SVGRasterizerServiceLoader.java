package org.eclipse.swt.graphics;

/**
 * @since 3.128
 */
public class SVGRasterizerServiceLoader {

    private static ISVGRasterizer rasterizer;

    public static void register(ISVGRasterizer implementation) {
        rasterizer = implementation;
    }

    public static ISVGRasterizer getRasterizer() {
        return rasterizer;
    }
}