import Text.XML.HaXml hiding(when)
import Data.List
import System.Environment
import Control.Monad
import System.Directory

directory = "src/main/assets/"

main = do
	[lang] <- getArgs
	str <- readFile $ "lojban_" ++ lang ++ ".xml"
	let	Document _ _ topElem _ = xmlParse lang str
		lojen = head $ childrenE topElem
		body = intercalate "\n" $ map showElement $
			filter isGismu $ childrenE lojen
		cont = mkXmlFromBody body
		body2 = intercalate "\n" $ map showElement $
			filter isCmavo $ childrenE lojen
		cont2 = mkXmlFromBody body2
	createDirectoryIfNotExist (directory ++ "gismu/")
	createDirectoryIfNotExist (directory ++ "cmavo/")
	writeFile (directory ++ "gismu/" ++ lang ++ ".xml") cont
	writeFile (directory ++ "cmavo/" ++ lang ++ ".xml") cont2

mkXmlFromBody :: String -> String
mkXmlFromBody body = header ++ "<direction from=\"lojban\" to=\"English\">" ++
	body ++ "</direction>\n"

createDirectoryIfNotExist :: FilePath -> IO ()
createDirectoryIfNotExist dir = do
	b <- doesDirectoryExist dir
	when (not b) $ createDirectory dir

isGismu :: Element i -> Bool
isGismu (Elem _ attrs _) =
	maybe False ((== "gismu") . show) $ lookup (N "type") attrs

isCmavo (Elem _ attrs _) =
	maybe False ((== "cmavo") . show) $ lookup (N "type") attrs

header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"

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
