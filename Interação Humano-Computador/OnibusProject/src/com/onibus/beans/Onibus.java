package com.onibus.beans;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.panelgrid.PanelGrid;

import com.googlecode.gmaps4jsf.component.map.Map;
import com.googlecode.gmaps4jsf.component.marker.Marker;
import com.googlecode.gmaps4jsf.component.window.HTMLInformationWindow;

@ManagedBean
public class Onibus {

	private String local;
	private String linha;
	private java.util.Map<String, SelectItem> itemLinhas;
	private List<SelectItem> linhas;

	public Onibus() {
		try {
			initLinhas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getLinha() {
		return linha;
	}

	public void setLinha(String linha) {
		this.linha = linha;
	}

	public List<SelectItem> getLinhas() {
		return linhas;
	}

	public void setLinhas(List<SelectItem> linhas) {
		this.linhas = linhas;
	}

	public void pesquisarRua(ActionEvent event) throws Exception {
		PanelGrid panelOnibus = (PanelGrid) FacesContext.getCurrentInstance()
				.getViewRoot().findComponent("formPanel:panelOnibus");
		panelOnibus.getChildren().clear();
		File file = new File(
				"C:\\Users\\Bruno\\Documents\\FATEC\\3º Módulo\\Interação Humano-Computador\\linhas de onibus");
		if (file.exists()) {
			File[] files = file.listFiles();
			for (File f : files) {
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				StringBuilder sb = new StringBuilder();
				String linha = null;
				while ((linha = br.readLine()) != null) {
					sb.append(linha);
				}
				JSONObject obj = new JSONObject(sb.toString());
				JSONArray array = obj.getJSONArray("itinerary");
				for (int count = 0; count < array.length(); count++) {
					String localOnibus = array.getString(count);
					if (localOnibus.contains(local.toUpperCase())) {
						OutputLabel label = new OutputLabel();
						label.setValue(obj.getString("name"));
						panelOnibus.getChildren().add(label);
						break;
					}
				}
				br.close();
				fr.close();
			}
		}
	}

	public void pesquisarLinha(ActionEvent event) throws Exception {
		Map map = (Map) FacesContext.getCurrentInstance().getViewRoot()
				.findComponent("form:map");
		File file = new File(
				"C:\\Users\\Bruno\\Documents\\FATEC\\3º Módulo\\Interação Humano-Computador\\linhas de onibus\\"
						+ linhas.get(linhas.indexOf(linha)).getLabel()
						+ ".json");
		if (file.exists()) {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			StringBuilder sb = new StringBuilder();
			String linha = null;
			while ((linha = br.readLine()) != null) {
				sb.append(linha);
			}
			JSONObject obj = new JSONObject(sb.toString());
			JSONArray array = obj.getJSONArray("itinerary");
			for (int count = 0; count < array.length(); count++) {
				String localOnibus = array.getString(count);
				if (count == 0) {
					map.setAddress(localOnibus
							.concat(", São José dos Campos, SP"));
				}
				Marker marker = new Marker();
				marker.setAddress(localOnibus
						.concat(", São José dos Campos, SP"));
				HTMLInformationWindow window = new HTMLInformationWindow();
				window.setHtmlText(marker.getAddress());
				marker.getChildren().add(window);
				map.getChildren().add(marker);
			}
			br.close();
			fr.close();
		}
	}

	private void initLinhas() throws HeadlessException, IOException {
		int c = 0;
		File file = new File(
				"C:\\Users\\Bruno\\Documents\\FATEC\\3º Módulo\\Interação Humano-Computador\\linhas de onibus");
		if (file.exists()) {
			File[] files = file.listFiles();
			itemLinhas = new LinkedHashMap<String, SelectItem>();
			for (File f : files) {
				if (!f.getName().equals("load.sh")) {
					SelectItem item = new SelectItem();
					item.setLabel(f.getName().replace(".json", "")
							.replace("-", " - ").replace("_", " - "));
					itemLinhas.put(String.valueOf(c), item);
					c++;
				}
			}
			linhas = new ArrayList<SelectItem>(itemLinhas.values());
		}
	}

}