package com.megumi.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**用户抢红包类信息
 * @author chenj
 */
public class UserRedPacket implements Serializable {
    private static final long serialVersionUID = -7530935013881725363L;
    private Long id;
    private Long redPackedId;
    private Long userId;
    private Double amount;
    private Timestamp grabTime;
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRedPackedId() {
        return redPackedId;
    }

    public void setRedPackedId(Long redPackedId) {
        this.redPackedId = redPackedId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Timestamp getGrabTime() {
        return grabTime;
    }

    public void setGrabTime(Timestamp grabTime) {
        this.grabTime = grabTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
