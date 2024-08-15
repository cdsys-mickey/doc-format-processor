package kcworks.docs.docfomat;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DocFormatMeta {
	private String contentType;
	private String fullText;
	private List<String> extensions;
	private String fileName;
	private Boolean extMatched;

}
