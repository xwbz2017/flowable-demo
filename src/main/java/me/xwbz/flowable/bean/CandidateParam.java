package me.xwbz.flowable.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.xwbz.flowable.bean.enums.CandidateType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateParam implements Serializable {
    private static final long serialVersionUID = 994290783964598993L;

    private String id;

    private String name;

    /** 默认是用户 */
    private CandidateType type = CandidateType.USER;
}
