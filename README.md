# evcalc
Shared expenses handling tool

# Usage examples

evcalc event list
evcalc event add <title> <date>
evcalc event drop <id>
evcalc event info
evcalc event select <id>
evcalc event calc

evcalc party add <name>
evcalc party drop <name>
evcalc party use <name> <expense>
evcalc party unuse <name> <expense>
evcalc party info <name>
evcalc party merge <name> <merge_name>
evcalc party unmerge <name> <merge_name>

evcalc expense add <title>
evcalc expense drop <title>

evcalc entry add <from> <to> <amount>
evcalc entry drop <from> <to> <amount>
evcalc entry list

'From' and 'To' values should be started with '@' (for party) or '%' (for expense) prefix.
Also '%cashier' entry part may be used
