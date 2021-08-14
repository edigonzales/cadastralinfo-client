package ch.so.agi.cadastralinfo.models.oereb;

import java.util.Map;

public class ReferenceWMS {
    private String baseUrl;
    
    private String layers;
    
    private Map<String,String> params;
    
    private String imageFormat;
    
    private double layerOpacity;
    
    private int layerIndex;
    
    private String legendAtWeb;
    
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getLayers() {
        return layers;
    }

    public void setLayers(String layers) {
        this.layers = layers;
    }

    public Map<String,String> getParams() {
        return params;
    }

    public void setParams(Map<String,String> params) {
        this.params = params;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public double getLayerOpacity() {
        return layerOpacity;
    }

    public void setLayerOpacity(double layerOpacity) {
        this.layerOpacity = layerOpacity;
    }

    public int getLayerIndex() {
        return layerIndex;
    }

    public void setLayerIndex(int layerIndex) {
        this.layerIndex = layerIndex;
    }

    public String getLegendAtWeb() {
        return legendAtWeb;
    }

    public void setLegendAtWeb(String legendAtWeb) {
        this.legendAtWeb = legendAtWeb;
    }

    @Override
    public String toString() {
        return "ReferenceWMS [baseUrl=" + baseUrl + ", layers=" + layers + ", imageFormat=" + imageFormat
                + ", layerOpacity=" + layerOpacity + ", layerIndex=" + layerIndex + ", legendAtWeb=" + legendAtWeb
                + "]";
    }
}
