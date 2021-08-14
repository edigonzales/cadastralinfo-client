package ch.so.agi.cadastralinfo;

public class TypeTuple {
    private String typeCode;
    private String typeCodeList;

    public TypeTuple() {}
    
    public TypeTuple(String typeCode, String typeCodeList) {
        this.typeCode = typeCode;
        this.typeCodeList = typeCodeList;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeCodeList() {
        return typeCodeList;
    }

    public void setTypeCodeList(String typeCodeList) {
        this.typeCodeList = typeCodeList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((typeCode == null) ? 0 : typeCode.hashCode());
        result = prime * result + ((typeCodeList == null) ? 0 : typeCodeList.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TypeTuple other = (TypeTuple) obj;
        if (typeCode == null) {
            if (other.typeCode != null)
                return false;
        } else if (!typeCode.equals(other.typeCode))
            return false;
        if (typeCodeList == null) {
            if (other.typeCodeList != null)
                return false;
        } else if (!typeCodeList.equals(other.typeCodeList))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TypeTuple [typeCode=" + typeCode + ", typeCodeList=" + typeCodeList + "]";
    }
}