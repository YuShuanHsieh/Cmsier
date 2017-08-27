package system;

public class SystemSettings {

  public final static String configXMLFile = "config/config.xml";
  public final static String ftpXMLFile = "config/ftp_config.xml";
  
  /* Default name of directories */
  public final static String D_config = "config";
  public final static String D_category = "category";
  public final static String D_draft = "draft";
  public final static String D_edit = "edit";
  public final static String D_css = "css";
  public final static String D_upload = "upload";
  public final static String D_web = "web";
  public final static String D_template = "template";
  public final static String D_sub_page = "page";
  public final static String D_root = "/Documents/CMS/"; /* for macOS */
  public final static String D_layout = "layout";
  public final static String D_layout_template = "CssTemplate";
  public final static String D_layout_xml = "CssXML";
  public final static String D_layout_preview = "preview";
  public final static String D_ftp = "public_html";
  
  public final static String CSSxmlPath = D_layout + "/";
  public final static String CSSpreviewPath = D_layout + "/" + D_layout_preview + "/";
  public final static String CSStemplatePath = D_layout + "/" + D_layout_template + "/";
  public final static String cssPath = D_css + "/";
  public final static String templatePath = D_template + "/";
  public final static String pagePath = D_edit + "/";
  public final static String categoryPath = D_category + "/";
  public final static String uploadPath = D_upload + "/";
  public final static String configurePath = D_config + "/";
}
