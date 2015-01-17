 package com.drdanick.McRKit.Backup;
 
 public class IndexTree
 {
   private IndexTreeNode rootNode;
   private String rootName;
   
   public IndexTree(String paramString)
   {
     this.rootNode = new IndexTreeNode();
     this.rootName = paramString;
   }
   
   public IndexTree(IndexTreeNode paramIndexTreeNode, String paramString)
   {
     this.rootNode = paramIndexTreeNode;
     this.rootName = paramString;
   }
   
   public IndexTreeNode getRootNode()
   {
     return this.rootNode;
   }
   
   public void addNode(String[] paramArrayOfString1, String[] paramArrayOfString2)
     throws IndexTreeException
   {
     if (!this.rootName.equals(paramArrayOfString1[0])) {
       throw new IndexTreeException("Root Node name mismatch for \"" + paramArrayOfString1[0] + "\". Expected: \"" + this.rootName + "\".");
     }
     addNode(this.rootNode, paramArrayOfString1, 1, paramArrayOfString2);
   }
   
   private void addNode(IndexTreeNode paramIndexTreeNode, String[] paramArrayOfString1, int paramInt, String[] paramArrayOfString2)
   {
     if (paramInt < paramArrayOfString1.length - 1) {
       addNode(paramIndexTreeNode.addChild(paramArrayOfString1[paramInt]), paramArrayOfString1, ++paramInt, paramArrayOfString2);
     } else {
       paramIndexTreeNode.addChild(paramArrayOfString1[paramInt], paramArrayOfString2);
     }
   }
   
   public String[] getValue(String[] paramArrayOfString)
     throws IndexTreeException
   {
     if (!this.rootName.equals(paramArrayOfString[0])) {
       throw new IndexTreeException("Root Node name mismatch for \"" + paramArrayOfString[0] + "\". Expected: \"" + this.rootName + "\".");
     }
     return getValue(this.rootNode, paramArrayOfString, 1);
   }
   
   private String[] getValue(IndexTreeNode paramIndexTreeNode, String[] paramArrayOfString, int paramInt)
   {
     if (paramInt < paramArrayOfString.length) {
       return getValue(paramIndexTreeNode.getChild(paramArrayOfString[paramInt]), paramArrayOfString, ++paramInt);
     }
     return paramIndexTreeNode.getValue();
   }
 }


/* Location:           C:\Users\ABC\Downloads\mods\mcrtoolkit10a15_3\serverdir\Minecraft_RKit.jar
 * Qualified Name:     com.drdanick.McRKit.Backup...IndexTree
 * JD-Core Version:    0.7.0.1
 */