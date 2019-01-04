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
public class BaseBean implements Serializable {

    private static final long serialVersionUID = 4229519121556691121L;

    private String id;

    private String name;
}
