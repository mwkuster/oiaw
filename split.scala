package eu.budabe.oiaw

object Utility {
  def splitBefore(lst : List[String], splitAt: String => Boolean) : List[List[String]] =  {
    def split_rec(lst : List[String], curr_lst : List[String], res : List[List[String]]) : List[List[String]] = {
	    lst match {
	      case x :: xs if splitAt(x) => {
          split_rec(xs, x :: List(), (curr_lst.reverse) :: res)
	      }
	      case x :: xs => {
          split_rec(xs, x :: curr_lst, res)
	      }
	      case Nil =>  (curr_lst.reverse :: res).reverse
	    }
    }
    
    split_rec(lst, List(), List())
  }
}
