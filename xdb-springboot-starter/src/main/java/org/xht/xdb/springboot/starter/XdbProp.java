package org.xht.xdb.springboot.starter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

@Slf4j
@ConfigurationProperties(prefix = "xdb")
@Data
public class XdbProp {
	private String sqlDir="files/sql";

	//Constructor(构造方法) -> @Autowired(依赖注入) -> @PostConstruct(注释的方法)
	@PostConstruct
	public void postConstruct() {
		log.debug("xdb.sqlDir: {}",sqlDir);
	}
}
