package me.xwbz.flowable.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1550774646040982490L;

    /** "身份信息"参数名称 */
    public static final String PRINCIPAL_ATTRIBUTE_NAME = User.class.getName() + ".PRINCIPAL";


    private String id;

    private String name;

    private String group;

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && this.id.equals(((User) obj).getId());
    }
}
