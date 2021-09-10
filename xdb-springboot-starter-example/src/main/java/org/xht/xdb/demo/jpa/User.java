package org.xht.xdb.demo.jpa;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;

@Data
@Entity
@Table(name = "T_TEST")
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false)
    private String ID;

    @Column(name = "MC")
    private String MC;

    @Column(name = "XH")
    private String XH;

    @Column(name = "XS")
    private String XS;

    @Column(name = "SMZQ")
    private String SMZQ;

    @Column(name = "SJ")
    private Date SJ;

    @Column(name = "RQ")
    private Date RQ;

}
