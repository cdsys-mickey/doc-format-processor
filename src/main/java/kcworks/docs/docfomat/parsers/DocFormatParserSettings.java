package kcworks.docs.docfomat.parsers;

import kcworks.string.StringValue;
import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class DocFormatParserSettings {
	public static final String PARAM_RETRIEVE_FULL_TEXT = "t";
	public static final String PARAM_COMPRESS_LINE_BREAKS = "n";
	public static final String PARAM_COMPRESS_SPACES = "s";
	public static final String PARAM_INPUT_LIMIT = "i";
	public static final String PARAM_OUTPUT_LIMIT = "o";
	public static final String PARAM_FETCH_EXT = "e";
	public static final String PARAM_DETECT_CONTENT_TYPE = "d";

	private boolean retrieveFullText = false;
	@Builder.Default
	private boolean compressLinkBreaks = true;
	@Builder.Default
	private boolean compressSpaces = true;
	private int inputMaxMegaBytes = 0;
	@Builder.Default
	private int outputMaxMegaBytes = 2048;
	private boolean fetchExtensions = false;
	private boolean detectContentType = false;

	public static DocFormatParserSettings forParsing(Map<String, String> params){
		DocFormatParserSettings result = new DocFormatParserSettings();
		return result.detectContentType().fetchExtensions().adapt(params);
	}

	public static DocFormatParserSettings forRetrieving(Map<String, String> params){
		DocFormatParserSettings result = new DocFormatParserSettings();
		return result.retrieveFullText().adapt(params);
	}

	public DocFormatParserSettings adapt(Map<String, String> params){
		if(params != null){
			// FullText
			Boolean retrieveFullText = StringValue.from(params.get(PARAM_RETRIEVE_FULL_TEXT)).guessBooleanObject();
			if(retrieveFullText != null){
				setRetrieveFullText(retrieveFullText);
			}
			// LineBreaks
			Boolean compressLinkBreaks = StringValue.from(params.get(PARAM_COMPRESS_LINE_BREAKS)).guessBooleanObject();
			if(compressLinkBreaks != null){
				setCompressLinkBreaks(compressLinkBreaks);
			}
			// Input
			Integer inputLimited = StringValue.from(params.get(PARAM_INPUT_LIMIT)).asInteger();
			if(inputLimited != null){
				setInputMaxMegaBytes(inputLimited);
			}
			// Output
			Integer outputLimited = StringValue.from(params.get(PARAM_OUTPUT_LIMIT)).asInteger();
			if(outputLimited != null){
				setOutputMaxMegaBytes(outputLimited);
			}
			// Ext
			Boolean fetchExt = StringValue.from(params.get(PARAM_FETCH_EXT)).guessBooleanObject();
			if(fetchExt != null){
				setFetchExtensions(fetchExt);
			}
			// Detect
			Boolean detectContentType = StringValue.from(params.get(PARAM_DETECT_CONTENT_TYPE)).guessBooleanObject();
			if(detectContentType != null ){
				setDetectContentType(detectContentType);
			}
		}
		return this;
	}

	public DocFormatParserSettings inputMaxMegaBytes(int length){
		this.inputMaxMegaBytes = length;
		return this;
	}

	public DocFormatParserSettings outputMaxMegaBytes(int length){
		this.outputMaxMegaBytes = length;
		return this;
	}

	public DocFormatParserSettings detectContentType() {
		this.detectContentType = true;
		return this;
	}

	public DocFormatParserSettings retrieveFullText(){
		this.retrieveFullText = true;
		return this;
	}

	public DocFormatParserSettings dontCompressLinkBreaks(){
		this.compressLinkBreaks = false;
		return this;
	}

	public DocFormatParserSettings dontCompressSpaces(){
		this.compressSpaces = false;
		return this;
	}

	public DocFormatParserSettings fetchExtensions(){
		this.fetchExtensions = true;
		return this;
	}
}
