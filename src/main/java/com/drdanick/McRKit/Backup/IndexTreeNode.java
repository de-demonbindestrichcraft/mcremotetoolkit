 package com.drdanick.McRKit.Backup;
 
 import java.util.HashMap;
 import java.util.Map;
 
 public class IndexTreeNode
 {
   private HashMap<String, IndexTreeNode> children = new HashMap();
   private String[] value;
   
   public IndexTreeNode()
   {
     this.value = null;
   }
   
   public IndexTreeNode(String[] paramArrayOfString)
   {
     this.value = paramArrayOfString;
   }
   
   public String[] getValue()
   {
     return this.value;
   }
   
   public IndexTreeNode getChild(String paramString)
   {
     return (IndexTreeNode)this.children.get(paramString);
   }
   
   public Map<String, IndexTreeNode> getChildren()
   {
     return this.children;
   }
   
   public boolean childExists(String paramString)
   {
     return this.children.containsKey(paramString);
   }
   
   public boolean isLeaf()
   {
     return this.value != null;
   }
   
   public IndexTreeNode addChild(String paramString, String[] paramArrayOfString)
   {
     if (!this.children.containsKey(paramString))
     {
       IndexTreeNode localIndexTreeNode = new IndexTreeNode(paramArrayOfString);
       this.children.put(paramString, localIndexTreeNode);
       return localIndexTreeNode;
     }
     return (IndexTreeNode)this.children.get(paramString);
   }
   
   public IndexTreeNode addChild(String paramString)
   {
     return addChild(paramString, null);
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.drdanick.McRKit.Backup...IndexTreeNode
 * JD-Core Version:    0.7.0.1
 */