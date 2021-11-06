package ai.fitme.ayahupgrade.bean;

import java.util.List;

public class OtaConfigBean {
    private boolean isNeedToUpgrade;
    private List<UpgradeFile> file;

    public boolean isNeedToUpgrade() {
        return isNeedToUpgrade;
    }

    public void setNeedToUpgrade(boolean needToUpgrade) {
        isNeedToUpgrade = needToUpgrade;
    }

    public List<UpgradeFile> getUpgradeFileList() {
        return file;
    }

    public void setUpgradeFileList(List<UpgradeFile> upgradeFileList) {
        this.file = upgradeFileList;
    }

    public class UpgradeFile{
        private String name;
        private String target_path;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTarget_path() {
            return target_path;
        }

        public void setTarget_path(String target_path) {
            this.target_path = target_path;
        }

        @Override
        public String toString() {
            return "UpgradeFile{" +
                    "name='" + name + '\'' +
                    ", target_path='" + target_path + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        if (null==file){
            return "OtaConfigBean{" +
                    "isNeedToUpgrade=" + isNeedToUpgrade +
                    '}';
        }
        return "OtaConfigBean{" +
                "isNeedToUpgrade=" + isNeedToUpgrade +
                ", upgradeFileList=" + file.toString() +
                '}';
    }
}
