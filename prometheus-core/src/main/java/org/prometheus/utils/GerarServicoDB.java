package org.prometheus.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class GerarServicoDB {

	private static final String VIEW_NAME = "SituacaoEntregaIndicadorDTO";
	private static final String SERVICE_NAME = "SituacaoEntregaIndicadorService";

	@Autowired
	private DatabaseService databaseService;

	public static void main(String[] args) {

		ApplicationContext ctxt = new ClassPathXmlApplicationContext("applicationContext.xml");

		GerarDTO gto = ctxt.getBean(GerarDTO.class);
		gto.gerar(VIEW_NAME);

		GerarServicoDB g = ctxt.getBean(GerarServicoDB.class);
		g.gerar(SERVICE_NAME, VIEW_NAME);

		((ConfigurableApplicationContext) ctxt).close();

	}

	public void gerar(String name, String dtoName) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			File f = new File("target/" + name + ".java");

			FileWriter fw = new FileWriter(f);

			// abre a classe
			fw.write("package org.prometheus.service;\n");
			fw.write("\n");
			fw.write("import java.sql.Connection;\n");
			fw.write("import java.sql.PreparedStatement;\n");
			fw.write("import java.sql.ResultSet;\n");
			fw.write("import java.sql.SQLException;\n");
			fw.write("import java.util.ArrayList;\n");
			fw.write("import java.util.List;\n");
			fw.write("\n");
			fw.write("import org.springframework.beans.factory.annotation.Autowired;\n");
			fw.write("import org.springframework.stereotype.Component;\n");
			fw.write("\n");
			fw.write("import org.prometheus.dto." + dtoName + ";\n\n");
			fw.write("import org.prometheus.utils.DBUtil;\n");
			fw.write("import org.prometheus.utils.DatabaseService;\n\n");			
			
			fw.write("@Component\n");
			fw.write("public class " + name + " {\n\n");
			fw.write("\n");
			fw.write("    @Autowired\n");
			fw.write("    private DatabaseService databaseService;\n");
			fw.write("\n\n");

			StringBuilder sb = new StringBuilder();

			sb.append("	public List<" + dtoName + "> listar() { \n");
			sb.append("		List<" + dtoName + "> result = new ArrayList<" + dtoName + ">(); \n");
			sb.append(" \n");
			sb.append("		Connection conn = null; \n");
			sb.append("		PreparedStatement pstmt = null; \n");
			sb.append("		ResultSet rs = null; \n");
			sb.append(" \n");
			sb.append("		try { \n");
			sb.append(" \n");
			sb.append("			conn = databaseService.getDataSource().getConnection(); \n");
			sb.append(" \n");
			sb.append("			pstmt = conn.prepareStatement(getDML()); \n");
			sb.append(" \n");
			sb.append("			rs = pstmt.executeQuery(); \n");
			sb.append(" \n");
			sb.append("			while (rs.next()) { \n");
			sb.append(" \n");
			sb.append("				" + dtoName + " dto = new " + dtoName + "(); \n");
			sb.append(" \n");

			conn = databaseService.getDataSource().getConnection();

			pstmt = conn.prepareStatement(getDML());

			rs = pstmt.executeQuery();

			// fields
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				String type = parseJavaAttributeType(rs.getMetaData().getColumnName(i),
						rs.getMetaData().getColumnTypeName(i));

				String name1 = parseJavaAttributeName(rs.getMetaData().getColumnName(i));
				name1 = name1.toUpperCase().charAt(0) + name1.substring(1);

				if ("BigDecimal".equalsIgnoreCase(type)) {
					sb.append(String.format("dto.set%s(rs.get%s(\"%s\"));\n", //
							name1, type, rs.getMetaData().getColumnName(i)));
				}

				else if ("Date".equalsIgnoreCase(type)) {
					sb.append(String.format("dto.set%s(rs.get%s(\"%s\"));\n", //
							name1, type, rs.getMetaData().getColumnName(i)));
				}

				else if ("Timestamp".equalsIgnoreCase(type)) {
					sb.append(String.format("dto.set%s(rs.get%s(\"%s\"));\n", //
							name1, type, rs.getMetaData().getColumnName(i)));
				}

				else {
					sb.append(String.format("dto.set%s(rs.get%s(\"%s\"));\n", //
							name1, type, rs.getMetaData().getColumnName(i)));
				}
			}
			sb.append(" \n");
			sb.append("				result.add(dto); \n");
			sb.append("			} \n");
			sb.append("		} catch (SQLException e) { \n");
			sb.append("			e.printStackTrace(); \n");
			sb.append("		} \n");
			sb.append("		finally { \n");
			sb.append("			DBUtil.close(conn, pstmt, rs); \n");
			sb.append("		} \n");
			sb.append(" \n");
			sb.append("		return result; \n");
			sb.append("	} \n");

			getDMLToStringBuilder(sb);

			fw.write(sb.toString());

			// fecha a classe
			fw.write("\n}\n");

			fw.flush();
			fw.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn, pstmt, rs);
		}

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
			return "BigDecimal";
		}

		else if ("date".equalsIgnoreCase(columnTypeName)) {
			return "Date";
		}

		else if (columnTypeName.startsWith("timestamp")) {
			return "Timestamp";
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

	private void getDMLToStringBuilder(StringBuilder sb) {

		sb.append("	public String getDML() { \n\n");
		sb.append("	  StringBuilder sb = new StringBuilder();\n\n");

		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(new File("/projetos/caixa/sigct/Trabalho/template.sql")));

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append("    sb.append(\" " + line + "\");\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		sb.append("\n");
		sb.append("	  return sb.toString();\n");
		sb.append("	}\n");
	}

}
