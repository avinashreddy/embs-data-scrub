package crux.embs.schemagen;

import edu.emory.mathcs.backport.java.util.Collections;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
@Deprecated
public class FileToTableMap {

    private static final Map<String, String> map = new TreeMap<>();

    static {
        map.put("PREFIX", "Prefix");
        map.put("ARMINDEX", "ARMIndex");
        map.put("ARMMTRX", "ARMMtrx");
        map.put("ARMMTRXG", "ARMMtrxG");
        map.put("SEC", "Sec");
        map.put("SC", "SecCurr");
        map.put("ISS", "Issuer");
        map.put("FH", "Hist");
        map.put("ARM", "AdjRate");
        map.put("GEO", "GeoLnDst");
        map.put("LDST", "LoanDist");
        map.put("SVC", "Servicer");
        map.put("QRT", "Quartile");
        map.put("CS2", "CollSum2");
        map.put("ARST", "ARMReset");
        map.put("LDS2", "LoanDist2");
        map.put("LDS3", "LoanDist3");
        map.put("DELH", "DELHist");
        map.put("MSDT", "FhlModStepDt");
        map.put("MS", "FhlModStep");
        map.put("SEC", "Sec");
        map.put("SC", "SecCurr");
        map.put("FH", "Hist");
        map.put("ARM", "AdjRate");
        map.put("ISS", "Issuer");
        map.put("CS2", "CollSum2");
//        map.put("COLL", "IssColl");
//        map.put("COLR", "IssColl");
//        map.put("COLS", "IssColl");
//        map.put("COL2", "IssColl2");
        map.put("QRT", "Quartile");
        map.put("LDST", "LoanDist");
        map.put("GEO", "GeoLnDst");
        map.put("ARST", "ARMReset");
        map.put("SVC", "Servicer");
        map.put("CPNH", "CpnHist");
        map.put("LDS2", "LoanDist2");
        map.put("LDS3", "LoanDist3");
        map.put("DELH", "DELHist");
//        map.put("NLAD", "FnmAdjLoan");
//        map.put("NLLD", "FnmLoan");
//        map.put("LLM", "FhlLoan");
//        map.put("LLAM", "FhlAdjLoan");
//        map.put("LLA", "FhlAdjLoan");
//        map.put("LL", "FhlLoan");
        map.put("SOAM", "SecOptAttrMod");
        map.put("LLMD", "FhlLoanMod");
        map.put("LLMM", "FhlLoanMod");
        map.put("MSDT", "FhlModStepDt");
        map.put("MS", "FhlModStep");
        map.put("FNM_MODD", "SecOptAttrFnmMod"); // ??
        map.put("FNM_MODM", "SecOptAttrFnmMod"); // ??
        map.put("PREFIX", "Prefix");
        map.put("ARMINDEX", "ARMIndex");
        map.put("ARMMTRX", "ARMMtrx");
        map.put("ARMMTRXG", "ARMMtrxG");
        map.put("SECF", "Sec");
        map.put("SCF", "SecCurr");
        map.put("ISSF", "Issuer");
        map.put("FHF", "Hist");
        map.put("ARMF", "AdjRate");
        map.put("GEOF", "GeoLnDst");
        map.put("LDSF", "LoanDist");
        map.put("SVCF", "Servicer");
        map.put("QRTF", "Quartile");
        map.put("CS2F", "CollSum2");
        map.put("ARSF", "ARMReset");
    }

    public String getTable(String fileName) {
        String key = fileName;
        if(fileName.indexOf("_") > 0) {
            key = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf("."));
        }
        String val = map.get(key);
        Assert.notNull(StringUtils.hasText(val), "No val for key " + key);
        return val;
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(map);
    }
}
