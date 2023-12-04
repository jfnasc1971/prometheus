package org.prometheus.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.prometheus.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GerarDTO {

	private static final String DTO_NAME = "VigenciaMembrosEquipeDTO";

	@Autowired
	private DatabaseService databaseService;

	public static void main(String[] args) {
		GerarDTO gerarDTO = AppConfig.getApplicationContext().getBean(GerarDTO.class);
		gerarDTO.gerar(DTO_NAME);
	}

	public void gerar(String name) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			File f = new File(name + ".java");
			System.out.println(f.getAbsolutePath());

			FileWriter fw = new FileWriter(f);

			// abre a classe
			fw.write("package org.prometheus.dto;\n");
			fw.write("\n");
			fw.write("public class " + name + " extends BaseDTO {\n\n");
			fw.write("    private static final long serialVersionUID = 1L;\n\n");

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());

			rs = pstmt.executeQuery();

			// fields
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {

				fw.write(parseJavaField(rs.getMetaData().getColumnName(i), //
						rs.getMetaData().getColumnTypeName(i)) + "\n");
			}

			fw.write("\n");

			// getters and setters
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				fw.write(parseJavaSetter(rs.getMetaData().getColumnName(i), //
						rs.getMetaData().getColumnTypeName(i)) + "\n");
				fw.write(parseJavaGetter(rs.getMetaData().getColumnName(i), //
						rs.getMetaData().getColumnTypeName(i)) + "\n");
			}

			// fecha a classe
			fw.write("}\n");

			fw.flush();
			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

	}

	private String parseJavaGetter(String columnName, String columnTypeName) {

		StringBuilder sb = new StringBuilder();

		String name = parseJavaAttributeName(columnName);
		String type = parseJavaAttributeType(columnName, columnTypeName);

		sb.append(String.format("    public %s get%s(){\n", type, name.toUpperCase().charAt(0) + name.substring(1)));
		sb.append(String.format("        return this.%s;\n", name));
		sb.append("    }\n");

		return sb.toString();
	}

	private String parseJavaSetter(String columnName, String columnTypeName) {

		StringBuilder sb = new StringBuilder();

		String name1 = parseJavaAttributeName(columnName);
		String name2 = name1.toUpperCase().charAt(0) + name1.substring(1);
		String type = parseJavaAttributeType(columnName, columnTypeName);

		sb.append(String.format("    public void set%s(%s %s){\n", name2, type, name1));
		sb.append(String.format("        this.%s = %s;\n", name1, name1));
		sb.append("    }\n");

		return sb.toString();
	}

	private String parseJavaField(String columnName, String columnTypeName) {

		String template = "    private %s %s;";

		String name = parseJavaAttributeName(columnName);
		String type = parseJavaAttributeType(columnName, columnTypeName);

		return String.format(template, type, name);
	}

	private String parseJavaAttributeName(String columnName) {

		StringBuilder sb = new StringBuilder();

		String[] parts = columnName.split("_");
		for (int i = 0; i < parts.length; i++) {
			if (i == 0) {
				sb.append(parts[i]);
			} else {
				sb.append(parts[i].toUpperCase().charAt(0) + parts[i].substring(1));
			}
		}

		return sb.toString();
	}

	private String parseJavaAttributeType(String columnName, String columnTypeName) {

		if ("numeric".equalsIgnoreCase(columnTypeName)) {
			if (columnName.startsWith("nu_")) {
				return "Long";
			}
			return "java.math.BigDecimal";
		}

		else if ("date".equalsIgnoreCase(columnTypeName) || "timestamp".equalsIgnoreCase(columnTypeName)
				 || "timestamptz".equalsIgnoreCase(columnTypeName)) {
			return "java.util.Date";
		}

		return "String";
	}

	private String getDML() {

		StringBuilder sb = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(new File("/projetos/caixa/sigct/Trabalho/template.sql")));

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

}
