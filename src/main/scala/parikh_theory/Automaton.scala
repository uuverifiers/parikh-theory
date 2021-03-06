package uuverifiers.parikh_theory

/**
 An automaton as far as the Parikh Theory is concerned.
 */
trait Automaton {

  type State
  type Label

  /**
   * Iterate over automaton states
   */
  def states: Iterable[State]

  /**
   * Transitions are just tuples of from, label, to
   */
  type Transition = (State, Label, State)

  /**
   * Given a state, iterate over all outgoing transitions
   */
  def outgoingTransitions(from: State): Iterator[(State, Label)]

  /**
   * The unique initial state
   */
  val initialState: State

  /**
   * Test if state is accepting
   */
  def isAccept(s: State): Boolean

  /// Derived methods ///

  /**
   * Iterate over all transitions
   */
  def transitions: Iterator[Transition] =
    for (s1 <- states.iterator; (s2, lbl) <- outgoingTransitions(s1))
      yield (s1, lbl, s2)

  class AutomatonGraph(val aut: Automaton) extends Graphable[State, Label] {

    def allNodes() = states.toSeq
    def edges() = transitions.toSeq
    def transitionsFrom(node: State) =
      outgoingTransitions(node).map(t => (node, t._2, t._1)).toSeq
    // FIXME this is ugly we should *not* change type
    def subgraph(selectedNodes: Set[State]): Graphable[State, Label] =
      this.dropEdges(Set()).subgraph(selectedNodes)
    def dropEdges(edgesToDrop: Set[(State, Label, State)]) = {
      new MapGraph(edges.toSet &~ edgesToDrop)
    }

    def addEdges(edgesToAdd: Iterable[(State, Label, State)]) = {
      val selectedEdges: Set[(State, Label, State)] = this
        .edges()
        .toSet ++ edgesToAdd
      new MapGraph(selectedEdges.toSeq)
    }
  }

  lazy val toGraph = new AutomatonGraph(this)

  /**
   * Compute the product of two automata
   *  FIXME: should work on other types for state and label
    **/
  def *(that: Automaton): ProductAutomaton = ???

}

/**
 * The only thing special about a product automaton is that each transition
 * remembers the ones it came from.
  **/
trait ProductAutomaton extends Automaton {
  // states are product states
  // labels are product labels
}
