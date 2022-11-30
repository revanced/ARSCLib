package com.reandroid.lib.arsc.chunk;

import com.reandroid.lib.arsc.array.EntryBlockArray;
import com.reandroid.lib.arsc.base.Block;
import com.reandroid.lib.arsc.container.SpecTypePair;
import com.reandroid.lib.arsc.item.IntegerArray;
import com.reandroid.lib.arsc.item.IntegerItem;
import com.reandroid.lib.arsc.item.TypeString;
import com.reandroid.lib.arsc.value.EntryBlock;
import com.reandroid.lib.arsc.value.ResConfig;
import com.reandroid.lib.json.JsonItem;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TypeBlock extends BaseTypeBlock implements JsonItem<JSONObject> {
    private final IntegerItem mEntriesStart;
    private final ResConfig mResConfig;
    private final IntegerArray mEntryOffsets;
    private final EntryBlockArray mEntryArray;
    public TypeBlock() {
        super(ChunkType.TYPE, 2);
        this.mEntriesStart=new IntegerItem();
        this.mResConfig =new ResConfig();
        this.mEntryOffsets=new IntegerArray();
        this.mEntryArray=new EntryBlockArray(mEntryOffsets, getEntryCountBlock(), mEntriesStart);

        addToHeader(mEntriesStart);
        addToHeader(mResConfig);

        addChild(mEntryOffsets);
        addChild(mEntryArray);
    }
    public boolean isEmpty(){
        return getEntryBlockArray().isEmpty();
    }
    public boolean isDefault(){
        return getResConfig().isDefault();
    }
    public String getQualifiers(){
        return getResConfig().getQualifiers();
    }
    public void setQualifiers(String qualifiers){
        getResConfig().parseQualifiers(qualifiers);
    }
    public int countNonNullEntries(){
        return getEntryBlockArray().countNonNull();
    }
    public SpecTypePair getParentSpecTypePair(){
        Block parent=getParent();
        while (parent!=null){
            if(parent instanceof SpecTypePair){
                return (SpecTypePair)parent;
            }
            parent=parent.getParent();
        }
        return null;
    }
    public void cleanEntries(){
        PackageBlock packageBlock=getPackageBlock();
        List<EntryBlock> allEntries=listEntries(true);
        for(EntryBlock entryBlock:allEntries){
            if(packageBlock!=null){
                packageBlock.removeEntryGroup(entryBlock);
            }
            entryBlock.setNull(true);
        }
    }
    public void removeEntry(EntryBlock entryBlock){
        PackageBlock packageBlock=getPackageBlock();
        if(packageBlock!=null){
            packageBlock.removeEntryGroup(entryBlock);
        }
        entryBlock.setNull(true);
    }
    public EntryBlock getOrCreateEntry(short entryId){
        return getEntryBlockArray().getOrCreate(entryId);
    }
    public EntryBlock getEntry(short entryId){
        return getEntryBlockArray().getEntry(entryId);
    }
    public ResConfig getResConfig(){
        return mResConfig;
    }
    public EntryBlockArray getEntryBlockArray(){
        return mEntryArray;
    }
    public List<EntryBlock> listEntries(){
        return listEntries(false);
    }
    public List<EntryBlock> listEntries(boolean skipNullBlock){
        List<EntryBlock> results=new ArrayList<>();
        Iterator<EntryBlock> itr = getEntryBlockArray().iterator(skipNullBlock);
        while (itr.hasNext()){
            EntryBlock block=itr.next();
            results.add(block);
        }
        return results;
    }
    public EntryBlock getEntryBlock(int entryId){
        return getEntryBlockArray().get(entryId);
    }
    @Override
    void onSetEntryCount(int count) {
        getEntryBlockArray().setChildesCount(count);
    }
    @Override
    protected void onChunkRefreshed() {
        getEntryBlockArray().refreshCountAndStart();
    }
    @Override
    protected void onPreRefreshRefresh(){
        mResConfig.refresh();
        super.onPreRefreshRefresh();
    }
    @Override
    public JSONObject toJson() {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("id", getTypeId());
        TypeString typeString=getTypeString();
        if(typeString!=null){
            jsonObject.put("name", typeString.get());
        }
        jsonObject.put("config", getResConfig().toJson());
        jsonObject.put("entries", getEntryBlockArray().toJson());
        return jsonObject;
    }
    @Override
    public void fromJson(JSONObject json) {
        setTypeId((byte) json.getInt("id"));
        String name= json.optString("name");
        if(name!=null){
            setTypeName(name);
        }
        getEntryBlockArray().fromJson(json.getJSONArray("entries"));
        getResConfig().fromJson(json.getJSONObject("config"));
    }
    @Override
    public String toString(){
        StringBuilder builder=new StringBuilder();
        builder.append(getResConfig().toString());
        builder.append(" ");
        builder.append(super.toString());
        return builder.toString();
    }
}
