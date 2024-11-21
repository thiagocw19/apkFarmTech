package com.fertitech.farmtech;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHolder {
    private static DataHolder instance = new DataHolder();

    private Map<String, List<Float>> temperaturasPorData;
    private Map<String, List<Float>> umidadesPorData;
    private Map<String, List<Float>> umidadeSoloPorData;

    private DataHolder() {
        temperaturasPorData = new HashMap<>();
        umidadesPorData = new HashMap<>();
        umidadeSoloPorData = new HashMap<>();
    }

    public static DataHolder getInstance() {
        return instance;
    }

    // Getter e Setter para temperaturasPorData
    public Map<String, List<Float>> getTemperaturasPorData() {
        return temperaturasPorData;
    }

    public void setTemperaturasPorData(Map<String, List<Float>> temperaturasPorData) {
        this.temperaturasPorData = temperaturasPorData;
    }

    // Getter e Setter para umidadesPorData
    public Map<String, List<Float>> getUmidadesPorData() {
        return umidadesPorData;
    }

    public void setUmidadesPorData(Map<String, List<Float>> umidadesPorData) {
        this.umidadesPorData = umidadesPorData;
    }

    public Map<String, List<Float>> getUmidadeSoloPorData() {
        return umidadeSoloPorData;
    }

    public void setUmidadeSoloPorData(Map<String, List<Float>> umidadeSoloPorData) {
        this.umidadeSoloPorData = umidadeSoloPorData;
    }
}
