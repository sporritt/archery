package archery.server

import scala.collection.immutable.HashMap
import archery.RTree
import archery.Entry
import archery.Box

/**
 * A whole bunch of trees. There may be other plant life and animals of various sizes, but they are
 * not modelled here. Here we concentrate just on the trees.
 * 
 * Available operations:
 * 
 * list
 * add <name>
 * get <tree id>
 * remove <tree id>
 * search <treeid, Box>
 * insert <treeid, x, y, value>
 * remove: <treeid, entryid>
 */
class Forest[A] {
  
  lazy val trees:scala.collection.mutable.Map[String, TreeMeta[A]] = {
    new scala.collection.mutable.HashMap[String, TreeMeta[A]]
  }
  
  /**
   * Get a list of the meta data for all the trees in the forest.
   */
  def list:Iterable[TreeInfo[A]] = trees.values.map(m => m.info)
  
  /**
   * Add a new tree.  A new RTree is generated and this method returns (tree meta, tree).
   */
  def add(desc:String = "An RTree"):TreeInfo[A] = {
    val r = RTree.empty[A]
    val meta = new TreeMeta(r, desc=desc)
    trees.put(meta.info.id, meta)
    meta.info
  }
  
  /**
   * Remove the tree with the given id.
   */
  def remove(treeId: String) = {
    trees.remove(treeId)
  }
  
  /**
   * Get the tree with the given id. Return value is of type Option[RTree[A]] 
   */
  def get(treeId:String):Option[RTree[A]] = {
    trees.get(treeId) match {
      case t:Some[TreeMeta[A]] => Some(t.get.tree)
      case _ => None
    }
  }
  
  /**
   * gets info about the tree with the given id. currently this is just the tree's description.
   */
  def info(treeId:String):Option[TreeInfo[A]] = {
    trees.get(treeId) match {
      case t:Some[TreeMeta[A]] => Some(t.get.info)
      case _ => None
    }
  }
  
  /**
   * Insert the given value A into the tree at location [x,y]
   */
  def insert(tree: RTree[A], x: Float, y: Float, value: A):Unit = tree.insert(x, y, value)
  
  /**
   * Insert the given value A into the tree with the given id at location [x,y]
   */
  def insert(treeId: String, x: Float, y: Float, value: A):Unit = {
    trees.get(treeId) match {
      case t:Some[TreeMeta[A]] => {
        val newTree = t.get.tree.insert(x, y, value)
        val newMeta = t.get.copy(newTree)
        trees.put(newMeta.info.id, newMeta)
      }
      case _ => Nil
    }
  }  
  
  /**
   * Search a tree. Returns all entries contained within the given box.
   */
  def search(tree:RTree[A], space:Box):Seq[Entry[A]] = {
    tree.search(space)
  }
  
  /**
   * Search a tree by treeid. Returns all entries contained within the given box.
   */
  def search(treeId:String, space:Box):Seq[Entry[A]] = {
    get(treeId) match {
      case tree:Some[RTree[A]] => search(tree.get, space)
      case _ => Seq.empty
    }
  }
  
  /**
   * Remove the entry with the given id from the given tree.
   */
  def remove(tree:RTree[A], entryId:String) = {
    tree.remove(entryId)
  }

  /**
   * Remove the entry with the given id from the tree with the given id.
   */
  def remove(treeId:String, entryId:String) = {
    get(treeId) match {
      case tree:Some[RTree[A]] => tree.get.remove(entryId)
      case _ => Nil
    }
  }
  
  /**
   * Remove an entry from the tree with the given id.
   */
  def remove(treeId:String, entry:Entry[A]):Unit = {
    remove(treeId, entry.uuid)
  }
}

/**
 * info about a tree.
 */
case class TreeInfo[A](tree:RTree[A], val desc:String) {
  val id = tree.uuid
}

/**
 * object we store - the info and the tree, separately.
 */
case class TreeMeta[A](val tree:RTree[A], desc:String) {
  val info = new TreeInfo(tree, desc)
	def copy(newTree:RTree[A]) = {
	  new TreeMeta(newTree, desc)
	}
}