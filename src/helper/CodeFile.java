package helper;

import java.util.ArrayList;

public class CodeFile {

	private String fileName;
	private ArrayList<CodeVersion> codeVersions = new ArrayList<CodeVersion>();

	public CodeFile(String fileName, String fileContent) {
		this.fileName = fileName;
		String[] temp = fileContent.split("\n");
		for (int i = 0; i < temp.length; i++) {
			if (temp[i].startsWith("# ")) {
				codeVersions.add(new CodeVersion(temp[i].substring(2), temp[i + 1]));
			}

		}
	}

	public CodeFile(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void addCode(String code) {
		codeVersions.add(new CodeVersion(Integer.toString(codeVersions.size() + 1), code));
	}

	public String getCode(String version) {
		for (int i = 0; i < codeVersions.size(); i++) {
			if (codeVersions.get(i).getVersion().equals(version)) {
				return codeVersions.get(i).getCode();
			}
		}
		return null;
	}

	public String getLatestCode() {
		return codeVersions.get(codeVersions.size() - 1).getCode();
	}

	public String[] getVersionList() {
		String[] versionList = new String[codeVersions.size()];
		for (int i = 0; i < codeVersions.size(); i++) {
			versionList[i] = codeVersions.get(i).getVersion();
		}
		return versionList;
	}

	public String fileContent() {
		String fileContent = "";
		for (int i = 0; i < codeVersions.size(); i++) {
			fileContent += "# " + codeVersions.get(i).getVersion() + "\n";
			fileContent += codeVersions.get(i).getCode() + "\n";
		}
		return fileContent;
	}
}
