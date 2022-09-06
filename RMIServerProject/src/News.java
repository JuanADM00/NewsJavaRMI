import java.io.Serializable;

public class News implements Serializable{
    private String uniqueName, headline, author, content;
    private java.sql.Timestamp creationDate, lastModificationDate;
    public String getUniqueName() {
        return uniqueName;
    }
    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }
    public String getHeadline() {
        return headline;
    }
    public void setHeadline(String headline) {
        this.headline = headline;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public java.sql.Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(java.sql.Timestamp creationDate) {
        this.creationDate = creationDate;
    }
    public java.sql.Timestamp getLastModificationDate() {
        return lastModificationDate;
    }
    public void setLastModificationDate(java.sql.Timestamp lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
    @Override
    public String toString() {
        return "CODIGO: " + uniqueName + "\tAUTOR: " + author + "\tTITULAR: " + headline + "\tCONTENIDO: " + content + "\tFECHA DE CREACIÓN: " + creationDate.toString() + "\tÚLTIMA MODIFICACIÓN" + lastModificationDate.toString();
    }
    
}
