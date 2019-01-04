package me.xwbz.flowable.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReasonParam implements Serializable {
    private static final long serialVersionUID = 1103377284857630798L;

    private String text;

    private List<BaseBean> files;
}
