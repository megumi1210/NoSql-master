package org.example.pojo;

import java.io.Serializable;

/**
 * 用于测试对象的实例化
 * 对象可序列化必须实现此接口
 * @author chenj
 */
public class Role implements Serializable {
    private static final long serialVersionUID = 2143377893858993504L;

    private  long id;
    private String roleName;
    private String note;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
