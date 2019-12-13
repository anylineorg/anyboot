package org.anyboot.mail.util.pop3;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.anyline.entity.DataRow;
import org.anyline.entity.DataSet;
import org.anyline.util.BasicUtil;
import org.anyline.util.ConfigTable;
import org.anyline.util.DateUtil;
import org.anyline.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailUtil {
	private static Logger log = LoggerFactory.getLogger(MailUtil.class);
	
	public static final String ACCOUNT = ConfigTable.getString("ACCOUNT");
	//授权密码
	public static final String PASSWORD = ConfigTable.getString("AUTH_CODE");
	public static final String PROTOCOL  = "pop3";
	public static final String HOST = "pop3.163.com";
	public static final String PORT = "110";
	public static final String ATTACHMENT_DIR = ConfigTable.getString("ATTACHMENT_DIR");
	
	private static Properties props = null;  
	
	static{
		props = new Properties();  
        props.setProperty("mail.store.protocol", PROTOCOL);        
        props.setProperty("mail.pop3.port", PORT);           	  
        props.setProperty("mail.pop3.host", HOST);         
	}
      
    /**
     * 邮件列表
     * @return 邮件列表
     * @throws Exception Exception
     */
    public static DataSet resceive() throws Exception { 
        Session session = Session.getInstance(props);  
        Store store = session.getStore("pop3");  
        store.connect(ACCOUNT, PASSWORD); 

        //收件箱  
        Folder folder = store.getFolder("INBOX");  
        folder.open(Folder.READ_WRITE); 
          
        // 得到收件箱中的所有邮件,并解析  
        Message[] messages = folder.getMessages();  
        DataSet set = parseMessage(messages);  
        
        //释放资源  
        folder.close(true);  
        store.close();  
        return set;
    }  
      
    /** 
     * 解析邮件 要
     * @param messages 解析的邮件列表 
     * @return 解析邮件 
     * @throws MessagingException MessagingException
     * @throws IOException IOException
     */
    public static DataSet parseMessage(Message ...messages) throws MessagingException, IOException {  
    	DataSet set = new DataSet();
        if (messages == null || messages.length < 1){
        	log.info("[无未接收的邮件]");
        	return set;
        }
        int size = messages.length;
        for(int i=size -1; i<=0; i--){
        	DataRow row = new DataRow();
            MimeMessage msg = (MimeMessage) messages[i];  
            String sendTime = getSentDate(msg);
            log.info("[解析邮件][subject:{}][发送时间]:{}][是否已读:{}][是否包含附件:{}]",msg.getSubject(),sendTime,isSeen(msg),isContainAttachment(msg));
            row.put("TITLE",msg.getSubject());
            row.put("SEND_TIME",sendTime);//邮件发送时间
            boolean isContainerAttachment = isContainAttachment(msg);  
            if (isContainerAttachment) {  
                List<String> attachments = downloadAttachment(msg);  
                row.put("ATTACHMENT_LIST",attachments);
            }   
            seenMessage(msg);
            deleteMessage(msg);
            set.addRow(row);
        }
        return set;
    }  
    
    /**
     * 删除邮件
     * @param messages messages
     * @throws MessagingException MessagingException
     * @throws IOException IOException
     */
    public static void deleteMessage(Message ...messages) throws MessagingException, IOException {  
        if (messages == null || messages.length < 1)   
            throw new MessagingException("未找到要解析的邮件!");  
          
        for (int i = 0, count = messages.length; i < count; i++) {  
            Message message = messages[i];
            String subject = message.getSubject();
            message.setFlag(Flags.Flag.DELETED, true);
            System.out.println("[删除邮件]: " + subject);    
        }
    } 
    
    /**
     * 标记为已读
     * @param messages messages
     * @throws MessagingException MessagingException
     * @throws IOException IOException
     */
    public static void seenMessage(Message ...messages) throws MessagingException, IOException {  
        if (messages == null || messages.length < 1)   
            throw new MessagingException("未找到要解析的邮件!");  
          
        for (int i = 0, count = messages.length; i < count; i++) {  
            Message message = messages[i];
            String subject = message.getSubject();
            message.setFlag(Flags.Flag.SEEN, true);
            System.out.println("[标记为已读]: " + subject);    
        }
    } 
    /** 
     * 获得邮件主题 
     * @param msg 邮件内容 
     * @return 解码后的邮件主题 
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     * @throws MessagingException MessagingException
     */  
    public static String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {  
        return MimeUtility.decodeText(msg.getSubject());  
    }  
      
    /** 
     * 获得邮件发件人 
     * @param msg 邮件内容 
     * @return 姓名 
     * @throws MessagingException MessagingException
     * @throws UnsupportedEncodingException  UnsupportedEncodingException
     */  
    public static String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {  
        String from = "";  
        Address[] froms = msg.getFrom();  
        if (froms.length < 1)  
            throw new MessagingException("没有发件人!");  
          
        InternetAddress address = (InternetAddress) froms[0];  
        String person = address.getPersonal();  
        if (person != null) {  
            person = MimeUtility.decodeText(person) + " ";  
        } else {  
            person = "";  
        }  
        from = person + "<" + address.getAddress() + ">";  
          
        return from;  
    }  
      
    /** 
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人 
     *  Mail.RecipientType.TO  收件人 
     *  Mail.RecipientType.CC  抄送 
     *  Mail.RecipientType.BCC 密送  
     * @param msg 邮件内容 
     * @param type 收件人类型 
     * @return 收件人邮件地址, 收件邮件地址2, ... 
     * @throws MessagingException  MessagingException
     */  
    public static String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {  
        StringBuffer receiveAddress = new StringBuffer();  
        Address[] addresss = null;  
        if (type == null) {  
            addresss = msg.getAllRecipients();  
        } else {  
            addresss = msg.getRecipients(type);  
        }  
          
        if (addresss == null || addresss.length < 1)  
            throw new MessagingException("没有收件人!");  
        for (Address address : addresss) {  
            InternetAddress internetAddress = (InternetAddress)address;  
            receiveAddress.append(internetAddress.toUnicodeString()).append(",");  
        }  
          
        receiveAddress.deleteCharAt(receiveAddress.length()-1); //删除最后一个逗号  
          
        return receiveAddress.toString();  
    }  
      
    /** 
     * 获得邮件发送时间 
     * @param msg 邮件内容 
     * @return yyyy年mm月dd日 星期X HH:mm 
     * @throws MessagingException  MessagingException
     */  
    public static String getSentDate(MimeMessage msg) throws MessagingException {  
    	return getSentDate(msg,null);
    } 
    public static String getSentDate(MimeMessage msg, String pattern) throws MessagingException {  
        Date receivedDate = msg.getSentDate();  
        if (receivedDate == null)  
            return "";  
          
        if (pattern == null || "".equals(pattern))  
            pattern = DateUtil.FORMAT_DATE_TIME ;  
        return DateUtil.format(receivedDate,pattern);  
    }  
      
    /** 
     * 判断邮件中是否包含附件 
     * @param part 邮件内容 
     * @return 邮件中存在附件返回true，不存在返回false 
     * @throws MessagingException  MessagingException
     * @throws IOException IOException
     */  
    public static boolean isContainAttachment(Part part) throws MessagingException, IOException {  
        boolean flag = false;  
        if (part.isMimeType("multipart/*")) {  
            MimeMultipart multipart = (MimeMultipart) part.getContent();  
            int partCount = multipart.getCount();  
            for (int i = 0; i < partCount; i++) {  
                BodyPart bodyPart = multipart.getBodyPart(i);  
                String disp = bodyPart.getDisposition();  
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {  
                    flag = true;  
                } else if (bodyPart.isMimeType("multipart/*")) {  
                    flag = isContainAttachment(bodyPart);  
                } else {  
                    String contentType = bodyPart.getContentType();  
                    if (contentType.indexOf("application") != -1) {  
                        flag = true;  
                    }    
                      
                    if (contentType.indexOf("name") != -1) {  
                        flag = true;  
                    }   
                }  
                  
                if (flag) break;  
            }  
        } else if (part.isMimeType("message/rfc822")) {  
            flag = isContainAttachment((Part)part.getContent());  
        }  
        return flag;  
    }  
      
    /**  
     * 判断邮件是否已读  
     * @param msg 邮件内容  
     * @return 如果邮件已读返回true,否则返回false  
     * @throws MessagingException    MessagingException
     */  
    public static boolean isSeen(MimeMessage msg) throws MessagingException {  
        return msg.getFlags().contains(Flags.Flag.SEEN);  
    }  
      
    /** 
     * 判断邮件是否需要阅读回执 
     * @param msg 邮件内容 
     * @return 需要回执返回true,否则返回false 
     * @throws MessagingException  MessagingException
     */  
    public static boolean isReplySign(MimeMessage msg) throws MessagingException {  
        boolean replySign = false;  
        String[] headers = msg.getHeader("Disposition-Notification-To");  
        if (headers != null)  
            replySign = true;  
        return replySign;  
    }  
      
    /** 
     * 获得邮件的优先级 
     * @param msg 邮件内容 
     * @return 1(High):紧急  3:普通(Normal)  5:低(Low) 
     * @throws MessagingException   MessagingException
     */  
    public static String getPriority(MimeMessage msg) throws MessagingException {  
        String priority = "普通";  
        String[] headers = msg.getHeader("X-Priority");  
        if (headers != null) {  
            String headerPriority = headers[0];  
            if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)  
                priority = "紧急";  
            else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)  
                priority = "低";  
            else  
                priority = "普通";  
        }  
        return priority;  
    }   
      
    /** 
     * 获得邮件文本内容 
     * @param part 邮件体 
     * @param content 存储邮件文本内容的字符串 
     * @throws MessagingException  MessagingException
     * @throws IOException IOException
     */  
    public static void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {  
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断  
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;   
        if (part.isMimeType("text/*") && !isContainTextAttach) {  
            content.append(part.getContent().toString());  
        } else if (part.isMimeType("message/rfc822")) {   
            getMailTextContent((Part)part.getContent(),content);  
        } else if (part.isMimeType("multipart/*")) {  
            Multipart multipart = (Multipart) part.getContent();  
            int partCount = multipart.getCount();  
            for (int i = 0; i < partCount; i++) {  
                BodyPart bodyPart = multipart.getBodyPart(i);  
                getMailTextContent(bodyPart,content);  
            }  
        }  
    }  
    public static List<String> downloadAttachment(Part part) throws UnsupportedEncodingException, MessagingException,  
    				FileNotFoundException, IOException {
    	return downloadAttachment(part,ATTACHMENT_DIR,null);
    }  
    /**  
     * 保存附件  
     * @param part 邮件中多个组合体中的其中一个组合体  
     * @param dest  附件保存目录  
     * @param files  文件名
     * @return 附件列表
     * @throws UnsupportedEncodingException UnsupportedEncodingException  
     * @throws MessagingException  MessagingException
     * @throws FileNotFoundException  FileNotFoundException
     * @throws IOException  IOException
     */  
    
    public static List<String> downloadAttachment(Part part, String dest,List<String> files) throws UnsupportedEncodingException, MessagingException,  
            FileNotFoundException, IOException {  
    	if(BasicUtil.isEmpty(files)){
    		files = new ArrayList<String>();
    	}
    	if (part.isMimeType("multipart/*")) {  
            Multipart multipart = (Multipart) part.getContent();    //复杂体邮件  
            int partCount = multipart.getCount();  
            for (int i = 0; i < partCount; i++) {  
            	boolean result = false;
                BodyPart bodyPart = multipart.getBodyPart(i);  
                String disp = bodyPart.getDisposition();  
                String fileNM = decodeText(bodyPart.getFileName());;
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {  
                    InputStream is = bodyPart.getInputStream();  
                    result = saveFile(is, dest,fileNM);  
                } else if (bodyPart.isMimeType("multipart/*")) {  
                	downloadAttachment(bodyPart,dest,files);  
                } else {  
                    String contentType = bodyPart.getContentType();  
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {  
                        result = saveFile(bodyPart.getInputStream(),dest,fileNM);  
                    }  
                }  
                if(result){
                	files.add(fileNM);
                }
            }  
        } else if (part.isMimeType("message/rfc822")) {  
        	downloadAttachment((Part) part.getContent(),dest,files);  
        }  
    	return files;
    }  
      
    /**  
     * 读取输入流中的数据保存至指定目录  
     * @param is 输入流  
     * @param fileName 文件名  
     * @param destDir 文件存储目录  
     * @return return
     */  
    private static boolean saveFile(InputStream is, String destDir, String fileName)  
            throws FileNotFoundException, IOException {  
    	if (BasicUtil.isEmpty(fileName)) {
			return false;
		}
    	
    	File target = new File(destDir+fileName);
    	if(!FileUtil.exists(destDir)){
    		File dir = new File(destDir);
    		dir.mkdirs();
    	}
        BufferedInputStream bis = new BufferedInputStream(is);  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));  
        int len = -1;  
        while ((len = bis.read()) != -1) {  
            bos.write(len);  
            bos.flush();  
        }  
        bos.close();  
        bis.close();  
        return true;
    }  
      
    /** 
     * 文本解码 
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本 
     * @return 解码后的文本 
     * @throws UnsupportedEncodingException 
     */  
    public static String decodeText(String encodeText) throws UnsupportedEncodingException {  
        if (encodeText == null || "".equals(encodeText)) {  
            return "";  
        } else {  
            return MimeUtility.decodeText(encodeText);  
        }  
    }
}
