package org.eclipse.swt.graphics;

/**
 * @since 3.128
 */
public class SVGRasterizerServiceLoader {

    private static ISVGRasterizer rasterizer;

//    public static void loadService() {
//        ServiceLoader<ISVGRasterizer> serviceLoader = ServiceLoader.load(ISVGRasterizer.class);
//        for (ISVGRasterizer r : serviceLoader) {
//            rasterizer = r;
//            break;
//        }
//    }

    public static void register(ISVGRasterizer implementation) {
        rasterizer = implementation;
    }

    public static ISVGRasterizer getRasterizer() {
    	if(rasterizer == null) {
//    		SVGRasterizerServiceLoader.loadService();
    	}
        return rasterizer;
    }
}