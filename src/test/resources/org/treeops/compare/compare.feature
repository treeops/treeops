Feature: Compare

  Scenario: 
    When two trees are compared
    | left | left value | right | right value |result left | result right| 
    | name |  			| name  | 			  |            |             |
    | name |      value | name  |       value | value      | value       |
    | name | left value | name  | right value | left value | right value |
    | left |     		| right |       	  |            |         	 |
    | left | left value | right | right value | left value | right value |
      
  	Then the result as described
  
  
