import Text.XML.HaXml hiding (when)
import Data.List
import System.Environment
import Control.Monad
import System.Directory

directory = "src/main/assets/"

main = do
	[lang, chars] <- getArgs
	str <- readFile $ "lojban_" ++ lang ++ ".xml"
	let	Document _ _ topElem _ = xmlParse lang str
		lojen = myHead "lojen" $ childrenE topElem
		enloj = myHead "enloj" $ tail $ childrenE topElem
	createDirectoryIfNotExist $ directory ++ "loj" ++ lang
	createDirectoryIfNotExist $ directory ++ lang ++ "loj"
	mkFiles lojen "abcdefgijklmnoprstuvxyz" $ directory ++ "loj" ++ lang
	mkFiles enloj chars $ directory ++ lang ++ "loj"

myHead :: String -> [a] -> a
myHead emsg [] = error emsg
myHead _ (x : _) = x

createDirectoryIfNotExist :: FilePath -> IO ()
createDirectoryIfNotExist dir = do
	b <- doesDirectoryExist dir
	when (not b) $ createDirectory dir

mkFiles :: Element i -> String -> FilePath -> IO ()
mkFiles elem chars dir = do
	forM_ chars $ \c -> do
		writeFile (dir ++ "/" ++ [c] ++ ".xml") $ mkXmlString elem c
	writeFile (dir ++ "/rest.xml") $ mkXmlStringRest elem chars

mkXmlStringRest :: Element i -> String -> String
mkXmlStringRest elem cs =
	header ++ "<direction from=\"lojban\" to=\"English\">" ++ body ++
		"</direction>\n"
	where
	body = intercalate "\n" $ map showElement $ filter (notSelectWord cs) $
		childrenE elem

mkXmlString :: Element i -> Char -> String
mkXmlString elem c =
	header ++ "<direction from=\"lojban\" to=\"English\">" ++ body ++
		"</direction>\n"
	where
	body = intercalate "\n" $ map showElement $ filter (selectWord c) $
		childrenE elem

header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"

selectWord :: Char -> Element i -> Bool
selectWord c (Elem _ attrs _) =
	maybe False ((c ==) . myHead "selectWord" . show) $ lookup (N "word") attrs

notSelectWord :: String -> Element i -> Bool
notSelectWord cs (Elem _ attrs _) =
	maybe False ((`notElem` cs) . myHead "notSelectWord" . show) $ lookup (N "word") attrs

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

showContent :: Content i -> String
showContent (CElem elem info) = showElement elem
showContent (CString ws cd info) = cd
showContent (CRef ref info) = show ref
showContent (CMisc misc info) = "misc"

showElement :: Element i -> String
showElement (Elem name attrs conts) =
	"<" ++ showQName name ++ " " ++
	intercalate " " (map showAttr attrs) ++ ">" ++
	concatMap showContent conts ++
	"</" ++ showQName name ++ ">"

showQName :: QName -> String
showQName (N n) = n

showAttr :: Attribute -> String
showAttr (N name, attv) = name ++ "=\"" ++ show attv ++ "\""
