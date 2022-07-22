package client;

import java.io.File;
import java.util.HashMap;
import java.util.Collection;
import tools.StringUtil;
import java.util.Iterator;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import java.util.ArrayList;
import provider.MapleDataFileEntry;
import provider.MapleDataProviderFactory;
import provider.MapleData;
import java.util.List;
import java.util.Map;

public class SkillFactory{
    private static final Map<Integer, ISkill> skills = new HashMap<Integer, ISkill>();
    private static final Map<Integer, List<Integer>> summonSkills = new HashMap<Integer, List<Integer>>();
    private static final Map<Integer, SummonSkillEntry> summonSkillsInfo = new HashMap<Integer, SummonSkillEntry>();
    private static MapleDataProvider datasource = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Skill.wz"));
    private static final MapleData stringSkills = MapleDataProviderFactory.getDataProvider("String.wz").getData("Skill.img");
    
    public static void LoadSkillInformaion() {
        System.out.println("[正在加载] -> 游戏职业技能信息");
        final MapleDataProvider skillWZ = MapleDataProviderFactory.getDataProvider("Skill.wz");
        final MapleDataDirectoryEntry root = skillWZ.getRoot();
        for (final MapleDataFileEntry topDir : root.getFiles()) {
            if (topDir.getName().length() <= 8) {
                for (final MapleData data : skillWZ.getData(topDir.getName())) {
                    if (data.getName().equals((Object)"skill")) {
                        for (final MapleData subData : data) {
                            if (subData != null) {
                                final int skillId = Integer.parseInt(subData.getName());
                                final Skill skil = Skill.loadFromData(skillId, subData);
                                List<Integer> job = (List<Integer>)SkillFactory.summonSkills.get((Object)Integer.valueOf(skillId / 10000));
                                if (job == null) {
                                    job = new ArrayList<Integer>();
                                    SkillFactory.summonSkills.put(Integer.valueOf(skillId / 10000), job);
                                }
                                job.add(Integer.valueOf(skillId));
                                skil.setName(getName(skillId));
                                SkillFactory.skills.put(Integer.valueOf(skillId), skil);
                                final MapleData summon_data = subData.getChildByPath("summon/attack1/info");
                                if (summon_data == null) {
                                    continue;
                                }
                                final SummonSkillEntry sse = new SummonSkillEntry();
                                sse.attackAfter = (short)MapleDataTool.getInt("attackAfter", summon_data, 999999);
                                sse.type = (byte)MapleDataTool.getInt("type", summon_data, 0);
                                sse.mobCount = (byte)MapleDataTool.getInt("mobCount", summon_data, 1);
                                SkillFactory.summonSkillsInfo.put(Integer.valueOf(skillId), sse);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static final ISkill getSkill(final int id) {
        if (!SkillFactory.skills.isEmpty()) {
            return (ISkill)SkillFactory.skills.get((Object)Integer.valueOf(id));
        }
        return null;
    }
    public static ISkill getSkill1(int id) {
        ISkill ret = (ISkill) skills.get(Integer.valueOf(id));
        if (ret != null) {
            return ret;
        }
        synchronized (skills) {
            ret = (ISkill) skills.get(Integer.valueOf(id));
            if (ret == null) {
                int job = id / 10000;
                MapleData skillroot = datasource.getData(StringUtil.getLeftPaddedStr(String.valueOf(job), '0', 3) + ".img");
                MapleData skillData = skillroot.getChildByPath("skill/" + StringUtil.getLeftPaddedStr(String.valueOf(id), '0', 7));
                if (skillData != null) {
                    ret = Skill.loadFromData(id, skillData);
                }
                skills.put(Integer.valueOf(id), ret);
            }
            return ret;
        }
    }
    public static long getDefaultSExpiry(final ISkill skill) {
        if (skill == null) {
            return -1L;
        }
        return skill.isTimeLimited() ? (System.currentTimeMillis() + 2592000000L) : -1L;
    }
    
    public static final List<Integer> getSkillsByJob(final int jobId) {
        return (List<Integer>)SkillFactory.summonSkills.get((Object)Integer.valueOf(jobId));
    }
    
    public static final String getSkillName(final int id) {
        final ISkill skil = getSkill(id);
        if (skil != null) {
            return skil.getName();
        }
        return "";
    }
    
    public static final String getName(final int id) {
        String strId = Integer.toString(id);
        strId = StringUtil.getLeftPaddedStr(strId, '0', 7);
        final MapleData skillroot = SkillFactory.stringSkills.getChildByPath(strId);
        if (skillroot != null) {
            return MapleDataTool.getString(skillroot.getChildByPath("name"), "");
        }
        return "";
    }
    
    public static final SummonSkillEntry getSummonData(final int skillid) {
        return (SummonSkillEntry)SkillFactory.summonSkillsInfo.get((Object)Integer.valueOf(skillid));
    }
    
    public static final Collection<ISkill> getAllSkills() {
        return SkillFactory.skills.values();
    }
}
