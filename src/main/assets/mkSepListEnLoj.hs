import Text.XML.HaXml
import Data.List
import System.Environment

main = do
	(xml_file : cn : rest) <- getArgs
	let filterRule = case cn of
		"not" -> notSelectWord $ head rest
		[c] -> selectWord c
	str <- readFile xml_file
	let body = case xmlParse xml_file str of
		(Document _ _ elem@(Elem _ _ contents) _) ->
			intercalate "\n" $ map showElement $
				filter filterRule $
				childrenE $ (!! 1) $ childrenE elem
	putStr $ header ++ "<direction from=\"lojban\" to=\"English\">" ++ body ++
		"</direction>\n"

header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"

selectWord :: Char -> Element i -> Bool
selectWord c (Elem _ attrs _) =
	maybe False ((c ==) . head . show) $ lookup (N "word") attrs

notSelectWord :: String -> Element i -> Bool
notSelectWord cs (Elem _ attrs _) =
	maybe False ((`notElem` cs) . head . show) $ lookup (N "word") attrs

childrenE :: Element i -> [Element i]
childrenE (Elem _ _ cs) = map getElem $ filter isElem cs

isElem :: Content i -> Bool
isElem (CElem _ _) = True
isElem _ = False

getElem :: Content i -> Element i
getElem (CElem elem _) = elem
getElem _ = error "not CElem"

getN :: QName -> String
getN (N n) = n

showContent :: Show i => Content i -> String
showContent (CElem elem info) = showElement elem
showContent (CString ws cd info) = cd
showContent (CRef ref info) = show ref
showContent (CMisc misc info) = "misc"

showElement :: Show i => Element i -> String
showElement (Elem name attrs conts) =
	"<" ++ showQName name ++ " " ++
	intercalate " " (map showAttr attrs) ++ ">" ++
	concatMap showContent conts ++
	"</" ++ showQName name ++ ">"

showQName :: QName -> String
showQName (N n) = n

showAttr :: Attribute -> String
showAttr (N name, attv) = name ++ "=\"" ++ show attv ++ "\""
