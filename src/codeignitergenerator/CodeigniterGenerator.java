/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeignitergenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adiel
 */
public class CodeigniterGenerator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {

            carpetaraiz = System.getProperty("user.dir") + "/codigo/";
            File carpeta = new File(carpetaraiz);
            carpeta.mkdir();
            File modelos = new File(carpetaraiz + "/models/");
            modelos.mkdir();

            File controladores = new File(carpetaraiz + "/controllers/");
            controladores.mkdir();

            File vistas = new File(carpetaraiz + "/views/");
            vistas.mkdir();

            System.out.println(carpetaraiz);

            String database = "edyna";
            String url = "jdbc:postgresql://localhost/" + database;
            Properties props = new Properties();
            props.setProperty("user", "postgres");
            props.setProperty("password", "123");
            // props.setProperty("ssl","true");
            Connection conn = DriverManager.getConnection(url, props);

            List<String> resultSet = new ArrayList<String>();

            try {
                DatabaseMetaData metaData = conn.getMetaData();
                String[] types = {"TABLE"};
                ResultSet tables = metaData.getTables(null, null, "%", types);
                while (tables.next()) {
                    System.out.println(tables.getString("TABLE_NAME"));
                    ProcesaTabla(tables.getString("TABLE_NAME"), metaData, conn);

                }

            } catch (SQLException e) {
                // logger.error(e.toString());
                System.out.println(e.getMessage());
            }

        } catch (SQLException ex) {
            Logger.getLogger(CodeigniterGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static String carpetaraiz;

    private static void ProcesaTabla(String nombre, DatabaseMetaData metaData, Connection conn) throws SQLException {
        CreaModelos(nombre, metaData, conn);
        CreaControladores(nombre, metaData, conn);
        CreaVistas(nombre, metaData, conn);
    }

    private static void CreaModelos(String nombreTabla, DatabaseMetaData metaData, Connection conn) {
        try {
            //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            File modelo = new File(carpetaraiz + "/models/" + nombreTabla + "_Model.php");
            modelo.createNewFile();

            escribirModelo(modelo, nombreTabla, metaData, conn);

            ResultSet columnas = metaData.getColumns(null, null, nombreTabla, null);
            while (columnas.next()) {
                System.out.println(columnas.getString("COLUMN_NAME"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CodeigniterGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CodeigniterGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void CreaControladores(String nombreTabla, DatabaseMetaData metaData, Connection conn) {
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        try {
            //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            File controlador = new File(carpetaraiz + "/controllers/" + nombreTabla + ".php");
            controlador.createNewFile();

            escribirControlador(controlador, nombreTabla, metaData, conn);

            ResultSet columnas = metaData.getColumns(null, null, nombreTabla, null);
            while (columnas.next()) {
                System.out.println(columnas.getString("COLUMN_NAME"));
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(CodeigniterGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void CreaVistas(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws SQLException {
        try {

            File carpeta = new File(carpetaraiz + "/views/" + nombreTabla + "/");
            carpeta.mkdir();

            //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            vista_insertar(nombreTabla, metaData, conn);
            vista_cargar_modificar(nombreTabla, metaData, conn);
            vista_listado(nombreTabla, metaData, conn);
            vista_detalle(nombreTabla, metaData, conn);
//            vista_insertar(nombreTabla,  metaData,  conn);
//            vista_insertar(nombreTabla,  metaData,  conn);
//            vista_insertar(nombreTabla,  metaData,  conn);

        } catch (IOException ex) {
            Logger.getLogger(CodeigniterGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void escribirModelo(File modelo, String nombreTabla, DatabaseMetaData metaData, Connection conn) throws IOException, SQLException {
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        BufferedWriter bw = new BufferedWriter(new FileWriter(modelo));

        bw.write("<?php\n"
                + "\n class " + nombreTabla + " extends CI_Model{\n");
        bw.write("\n" + metodos_campos(nombreTabla, metaData, conn) + "\n");
        bw.write("\n" + metodos_insertar(nombreTabla, metaData, conn) + "\n");
        bw.write("\n" + metodos_modificar(nombreTabla, metaData, conn) + "\n");
        bw.write("\n" + metodos_eliminar(nombreTabla, metaData, conn) + "\n");
        bw.write("\n" + metodos_obtener_por_id(nombreTabla, metaData, conn) + "\n");
        bw.write("\n" + metodos_obtener_listado(nombreTabla, metaData, conn) + "\n");
        bw.write("\n" + metodos_count(nombreTabla, metaData, conn) + "\n");

        bw.write("}\n"
                + "?>\n");

        bw.close();

    }

    private static String metodos_campos(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws SQLException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        String res = "";
        ResultSet columnas = metaData.getColumns(null, null, nombreTabla, null);
        while (columnas.next()) {
            System.out.println("    var $" + columnas.getString("COLUMN_NAME") + ";\n");
        }
        res += "    function __construct() {\n"
                + "        parent::__construct();\n"
                + "    }\n"
                + "";

        return res;

    }

    private static String metodos_insertar(String nombreTabla, DatabaseMetaData metaData, Connection conn) {

        return "// add new " + nombreTabla + "\n"
                + "    function add() {"
                + " $this->db->insert('" + nombreTabla + "', $this);      \n"
                + "        return $this->db->insert_id();\n"
                + "}\n"
                + "";// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static String metodos_modificar(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws SQLException {
        String[] llaves = getKeys(nombreTabla, metaData, conn);
        String res = "function edit($data,$ids) {\n";
        for (String llave : llaves) {
            res += "$this->db->where('" + llave + "', $ids['" + llave + "']);\n";
        }

        res += "           $this->db->update('" + nombreTabla + "', $data);\n"
                + "        return true;"
                + "}\n";
        return res;
    }

    private static String metodos_eliminar(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws SQLException {

        String res = " // delete person by ids\n"
                + "    function delete($ids) {\n";
        String[] llaves = getKeys(nombreTabla, metaData, conn);

        for (String llave : llaves) {
            res += "$this->db->where('" + llave + "', $ids['" + llave + "']);\n";
        }

        res += "$this->db->delete('" + nombreTabla + "');\n"
                + "}\n";
        return res;

    }

    private static String metodos_obtener_por_id(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws SQLException {
        String res = "function get_by_id($ids) { \n";

        String[] llaves = getKeys(nombreTabla, metaData, conn);

        for (String llave : llaves) {
            res += "$this->db->where('" + llave + "', $ids['" + llave + "']);\n";
        }

        res += "      return $this->db->get('" + nombreTabla + "')->row();\n"
                + "}\n";
        return res;
    }

    private static String metodos_obtener_listado(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws SQLException {
        String res = " function get() {\n";
        String[] llaves = getKeys(nombreTabla, metaData, conn);

        for (String llave : llaves) {
            res += "$this->db->order_by('" + nombreTabla + "', 'asc');\n";
        }
        res += "return $this->db->get('" + nombreTabla + "')->result_array();"
                + "}\n";
        return res;
    }

    private static String metodos_count(String nombreTabla, DatabaseMetaData metaData, Connection conn) {
        //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return "function count_all(){\n"
                + "return $this->db->count_all('" + nombreTabla + "');\n"
                + "}\n";
    }

    private static String capitalize_first(String nombreTabla) {
        return nombreTabla; //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void escribirControlador(File controlador, String nombreTabla, DatabaseMetaData metaData, Connection conn) throws IOException, SQLException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(controlador));

        bw.write("<?php\n"
                + "require_once 'application/models/" + nombreTabla + "Model.php';\n\n"
                + "class " + capitalize_first(nombreTabla) + " extends CI_Controller{\n"
                + "function __construct() {\n"
                + "        parent::__construct();\n"
                + "}\n"
                + "function index() {\n"
                + "        $this->manage();\n"
                + "    }\n"
                + "    function bind($ClassName) {\n"
                + "        $rc = new ReflectionClass($ClassName);\n"
                + "        $instance = $rc->newInstance();\n"
                + "        $fields = $rc->getDefaultProperties();\n"
                + "        $fieldnames = array_keys($fields);\n"
                + "        $CI = & get_instance();   \n"
                + "        foreach ($fieldnames as $field) {\n"
                + "            $instance->{$field} = $CI->input->post($field);\n"
                + "        }       \n"
                + "        return $instance;\n"
                + "    }\n"
                + ""
                + ""
                + "function goIndex() {\n"
                + "        redirect(base_url() . 'index.php/" + nombreTabla + "/index');\n"
                + "    }");
        bw.write("\n" + controlador_metodos_insertar(nombreTabla, metaData, conn) + "\n");
        bw.write("\n" + controlador_metodos_modificar(nombreTabla, metaData, conn) + "\n");
        bw.write("\n" + controlador_metodos_eliminar(nombreTabla, metaData, conn) + "\n");
        bw.write("\n" + controlador_metodos_obtener_por_id(nombreTabla, metaData, conn) + "\n");
        bw.write("\n" + controlador_metodos_obtener_listado(nombreTabla, metaData, conn) + "\n");

        bw.write("}\n"
                + "?>\n");

        bw.close();
    }

    private static String controlador_metodos_insertar(String nombreTabla, DatabaseMetaData metaData, Connection conn) {
        String res = "function add() {\n"
                + "        $modelo = $this->bind('" + nombreTabla + "Model');\n"
                + "        if ($modelo->add() != -1) {\n"
                + "            goIndex()    ;\n"
                + "        } else {\n"
                + "            $this->data['custom_error'] = '<div class=\"form_error\"><p>An Error Occured.</p></div>';\n"
                + "        }\n"
                + "}\n"
                + ""
                + ""
                + "    function addview() {\n"
                + "        $this->load->view('" + nombreTabla + "_add');\n"
                + "    }\n"
                + "\n";

        return res;//  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static String controlador_metodos_modificar(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws SQLException {
        String[] llaves = getKeys(nombreTabla, metaData, conn);
        String parametros = "";
        String parametrosmodelo = "array(";
        String parametrosmodeloold = "array(";
        int contador = 0;
        for (String llave : llaves) {
            if (contador != 0) {
                parametros += " , ";
                parametrosmodelo += " , ";
                parametrosmodeloold += " , ";
            }
            parametros += "$" + llave;
            parametrosmodelo += "'" + llave + "'=>$" + llave;
            parametrosmodeloold += "'" + llave + "'=>$this->input->post('old" + llave + "')";

        }
        parametrosmodelo += ")";
        parametrosmodeloold += ")";

        String res = "function editview(" + parametros + ") {\n"
                + "\n"
                + "       $row = $this->CabeceraFacturaModel->get_by_id(" + parametrosmodelo + ");\n"
                + "  if($row){\n"
                + "$this->load->view('" + nombreTabla + "_edit',array('data'=> $row));\n"
                + "}else{\n"
                + "die('Wrong id');\n"
                + "} \n"
                + "    }\n"
                + ""
                + ""
                + ""
                + "    function edit() {\n"
                + "      $modelo = $this->bind('" + nombreTabla + "Model');\n"
                + "        if ($modelo->edit(" + parametrosmodeloold + ") != -1) {\n"
                + "            goIndex()    ;\n"
                + "        } else {\n"
                + "            $this->data['custom_error'] = '<div class=\"form_error\"><p>An Error Occured.</p></div>';\n"
                + "        }\n"
                + "    }\n"
                + "\n";//  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        return res;
    }

    private static String controlador_metodos_eliminar(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws SQLException {

        String[] llaves = getKeys(nombreTabla, metaData, conn);
        String parametros = "";
        String parametrosmodelo = "array(";
        int contador = 0;
        for (String llave : llaves) {
            if (contador != 0) {
                parametros += " , ";
                parametrosmodelo += " , ";
            }
            parametros += "$" + llave;
            parametrosmodelo += "'" + llave + "'=>$" + llave;

        }
        parametrosmodelo += ")";

        String res = "function delete(" + parametros + ") {\n"
                + "\n"
                + "        $this->" + nombreTabla + "Model->delete(" + parametrosmodelo + ");\n"
                + "        goIndex();\n"
                + "    }"
                + "";//  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        return res;
    }

    private static String controlador_metodos_obtener_por_id(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws SQLException {

        String[] llaves = getKeys(nombreTabla, metaData, conn);
        String parametros = "";
        String parametrosmodelo = "array(";
        int contador = 0;
        for (String llave : llaves) {
            if (contador != 0) {
                parametros += " , ";
                parametrosmodelo += " , ";
            }
            parametros += "$" + llave;
            parametrosmodelo += "'" + llave + "'=>$" + llave;

        }
        parametrosmodelo += ")";

        String res = "function get_by_id(" + parametros + ") {\n"
                + "\n"
                + "       $row = $this->CabeceraFacturaModel->get_by_id(" + parametrosmodelo + ");\n"
                + "  if($row){\n"
                + "$this->load->view('" + nombreTabla + "_detail',array('data'=>  $row));\n"
                + "}else{\n"
                + "die('Wrong id');\n"
                + "} \n"
                + "    }\n"
                + "";//  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        return res;
//return "";//  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static String controlador_metodos_obtener_listado(String nombreTabla, DatabaseMetaData metaData, Connection conn) {
        return "function lista() {\n"
                + ""
                + "$modelo = new nombreTabla();\n"
                + " $this->load->view('tbl_cabecera_factura_add',array('data'=>  $modelo->get()));\n"
                + "}\n";//  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void vista_insertar(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws IOException, SQLException {
        File controlador = new File(carpetaraiz + "/views/" + nombreTabla + "/" + nombreTabla + "_insert.php");
        controlador.createNewFile();

        BufferedWriter bw = new BufferedWriter(new FileWriter(controlador));

        bw.write("<form  method='post' action='<?php echo base_url().'/" + nombreTabla + "/add'; ?>' >\n<table>\n");
        ResultSet columnas = metaData.getColumns(null, null, nombreTabla, null);

        while (columnas.next()) {
            bw.write("<tr>\n");
            System.out.println(columnas.getString("COLUMN_NAME"));
            bw.write("<td>" + capitalize_first(columnas.getString("COLUMN_NAME")) + ":</td>\n");
            bw.write("<td><input type='text' name='" + columnas.getString("COLUMN_NAME") + "' /><td>\n");
            bw.write("</tr>\n");
        }

        bw.write("<tr>\n<td></td>\n<td><input type='submit' /><td>\n</tr>\n");
        bw.write("</table>\n</form>\n");
        bw.close();
    }

    private static void vista_cargar_modificar(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws IOException, SQLException {
        File controlador = new File(carpetaraiz + "/views/" + nombreTabla + "/" + nombreTabla + "_edit.php");
        controlador.createNewFile();

        BufferedWriter bw = new BufferedWriter(new FileWriter(controlador));

//        bw.write("<form  method='post' action='' ><table>");
        bw.write("<form  method='post' action='<?php echo base_url().'/" + nombreTabla + "/edit'; ?>' >\n<table>\n");

        String[] llaves = getKeys(nombreTabla, metaData, conn);
        for (String llave : llaves) {
            bw.write("<input type='hidden' name='old"+llave+"' value='<?php echo $data->" + llave + " ;  ?>' />\n");
        }
        
        
        
        ResultSet columnas = metaData.getColumns(null, null, nombreTabla, null);

        while (columnas.next()) {
            bw.write("<tr>\n");
//            System.out.println(columnas.getString("COLUMN_NAME"));
            bw.write("<td>" + capitalize_first(columnas.getString("COLUMN_NAME")) + ":</td>\n");
            bw.write("<td><input type='text' name='" + columnas.getString("COLUMN_NAME") + "' value='<?php echo $data->" + columnas.getString("COLUMN_NAME") + " ;  ?>' /><td>\n");
            bw.write("</tr>\n");
        }

        bw.write("<tr>\n<td></td>\n<td><input type='submit' /><td>\n</tr>\n");
        bw.write("</table>\n</form>\n");
        bw.close();
    }

    private static void vista_listado(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws IOException, SQLException {
        File controlador = new File(carpetaraiz + "/views/" + nombreTabla + "/" + nombreTabla + "_list.php");
        controlador.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(controlador));

        bw.write("<table>\n");
        ResultSet columnas = metaData.getColumns(null, null, nombreTabla, null);
        bw.write("<tr>\n");
        while (columnas.next()) {
            System.out.println(columnas.getString("COLUMN_NAME"));
            bw.write("<td>" + columnas.getString("COLUMN_NAME") + "</td>\n");
        }
        bw.write("</tr>\n");

        bw.write("<?php\n"
                + "for($i=0;$i<count($data);$i++){\n"
                + "?>\n");
         columnas = metaData.getColumns(null, null, nombreTabla, null);
        while (columnas.next()) {
            System.out.println(columnas.getString("COLUMN_NAME"));
            bw.write("<td><?php  echo $data[$i]->" + columnas.getString("COLUMN_NAME") + "; ?><td>\n");
        }

        bw.write("</td>\n"
                + "<? }\n"
                + "?>\n");

        bw.write("</table>\n");
        bw.close();
    }

    private static void vista_detalle(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws IOException, SQLException {
        File controlador = new File(carpetaraiz + "/views/" + nombreTabla + "/" + nombreTabla + "_detail.php");
        controlador.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(controlador));

        bw.write("<table>\n");
        ResultSet columnas = metaData.getColumns(null, null, nombreTabla, null);

        while (columnas.next()) {
            bw.write("<tr>\n");
            System.out.println(columnas.getString("COLUMN_NAME"));
            bw.write("<td>" + capitalize_first(columnas.getString("COLUMN_NAME")) + ":</td>\n");
            bw.write("<td>"+ columnas.getString("COLUMN_NAME") +"<td>\n");
            bw.write("<td><?php echo $data->" + columnas.getString("COLUMN_NAME") + " ;  ?><td>\n");
            bw.write("</tr>\n");
        }

        bw.write("</table>\n");
        bw.close();
    }

    private static String[] getKeys(String nombreTabla, DatabaseMetaData metaData, Connection conn) throws SQLException {

        ResultSet columnas = metaData.getPrimaryKeys(null, null, nombreTabla);
        ArrayList<String> result = new ArrayList<String>();
        while (columnas.next()) {
            System.out.println("    var $" + columnas.getString("COLUMN_NAME") + ";\n");
            result.add(columnas.getString("COLUMN_NAME"));
        }

        return result.toArray(new String[]{});

    }
}
